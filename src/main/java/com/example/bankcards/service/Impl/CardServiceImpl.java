package com.example.bankcards.service.Impl;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.CARD_STATUS;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardException.CardNotFoundException;
import com.example.bankcards.exception.CardException.CardTransferMoneyException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.DAO.CardServiceDAO;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardServiceDAO{
    private final CardRepository repository;

    /**
     * Сохраняет информацию о карте в базе данных
     * 
     * @param card объект карты для сохранения
     * @return сохраненный объект карты
     */
    @Override
    public Card save(Card card){
        return repository.save(card);
    };


    /**
     * Создает новую карту в системе
     * 
     * @param card объект карты для создания
     * @return созданный объект карты
     */
    @Override
    public Card create(Card card){
        return save(card);
    };


    /**
     * Получает карту по идентификатору
     * 
     * @param cartId идентификатор карты
     * @return найденный объект карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public Card getCardById(Long  cartId){
        return repository.findById(cartId).orElseThrow(() -> new CardNotFoundException(cartId));
    };


    /**
     * Получает текущий баланс карты
     * 
     * @param cardId идентификатор карты
     * @return текущий баланс карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public  BigDecimal getBallance(Long cardId){
        return getCardById(cardId).getBallance();
    };



    /**
     * Проверяет принадлежит ли карта указанному пользователю
     * 
     * @param userId идентификатор пользователя
     * @param cardId идентификатор карты
     * @return true если карта принадлежит пользователю, иначе false
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
   public boolean checkUserCard(Long userId , Long cardId){
        return getCardById(cardId).getOwner().getId() == userId;
    };



    /**
     * Инициирует запрос на блокировку карты пользователем
     * 
     * @param cardId идентификатор карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public void userBlockCard(Long cardId){
        Card card = getCardById(cardId);
        card.setStatus(CARD_STATUS.PENDING_BLOCK);

        save(card);
    };


    /**
     * Получает страницу с картами пользователя
     * 
     * @param userId идентификатор пользователя
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return страница с картами пользователя в формате DTO
     */
    @Override
    public Page<CardDto> findUserCardsById(Long userId , int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Card> cardsPage = repository.findByOwnerId(userId, pageable);
    
        return cardsPage.map(this::convertToCardDto);
    };



    /**
     * Получает страницу с картами пользователя по статусу
     * 
     * @param userId идентификатор пользователя
     * @param status статус карты для фильтрации
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return страница с картами пользователя
     */
    @Override
    public Page<Card> findWithPagingCards(Long userId , CARD_STATUS status, int page , int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return repository.findByOwnerIdAndStatus(userId, status, pageable);
    };


    /**
     * Выполняет перевод денег между картами
     * 
     * @param fromId идентификатор карты отправителя
     * @param toId идентификатор карты получателя
     * @param sum сумма перевода
     * @throws CardNotFoundException если одна из карт не найдена
     * @throws CardTransferMoneyException если:
     *         - недостаточно средств на карте отправителя
     *         - карта не активна
     *         - карты совпадают
     *         - сумма перевода меньше или равна нулю
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void moneyTransfer(Long fromId , Long toId , BigDecimal sum){
        Card fromCard = repository.findByIdWithLock(fromId).orElseThrow(() -> new CardNotFoundException(fromId));
        Card toCard = repository.findByIdWithLock(toId).orElseThrow(() -> new CardNotFoundException(toId));

        if (fromCard.getBallance().compareTo(sum)  < 0 ) {
            throw new CardTransferMoneyException(fromId,toId , "Недостаточно средств");
        }

        if (fromCard.getStatus() != CARD_STATUS.ACTIVE || toCard.getStatus() != CARD_STATUS.ACTIVE ) {
            throw new CardTransferMoneyException(fromId,toId , "Карта должна быть активирована");
        }

        
        if (fromId.equals(toId) ) {
          throw new CardTransferMoneyException(fromId, toId , "Карты не должны быть одинаковые");
        }

        if (sum.compareTo(BigDecimal.ZERO) <= 0 ) {
            throw new CardTransferMoneyException(fromId, toId , "Сумма перевода должна быть больше нуля");
        }
        fromCard.setBallance(fromCard.getBallance().subtract(sum));
        toCard.setBallance(toCard.getBallance().add(sum));

        save(fromCard);
        save(toCard);
    }


    /**
     * Блокирует карту
     * 
     * @param cardId идентификатор карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public void block(Long cardId){
        Card card = getCardById(cardId);
        card.setStatus(CARD_STATUS.BLOCKED);

        save(card);
    };


    /**
     * Удаляет карту
     * 
     * @param id идентификатор карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public  void delete(Long id){
        if (!repository.existsById(id)) {
            throw new CardNotFoundException(id);
        }

        repository.deleteById(id);
    };


    /**
     * Активирует карту
     * 
     * @param id идентификатор карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public void activate(Long id){
        Card card = getCardById(id);
        card.setStatus(CARD_STATUS.ACTIVE);

        save(card);
    };


    /**
     * Получает страницу с картами по статусу
     * 
     * @param status статус карты для фильтрации
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return страница с картами в формате DTO
     */
    @Override
    public Page<CardDto> findByStatus(CARD_STATUS status , int page , int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Card> cardsPage = repository.findByStatus(status, pageable);

        return  cardsPage.map(this::convertToCardDto);
    };




    /**
     * Получает страницу со всеми картами в системе
     * 
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return страница со всеми картами в формате DTO
     */
    @Override
    public Page<CardDto>getAllCards(int page , int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Card> cardsPage = repository.findAll(pageable);

       
        return cardsPage.map(this::convertToCardDto);
    }





    /**
     * Преобразует объект Card в CardDto
     * 
     * @param card объект карты для преобразования
     * @return объект CardDto
     */
    private CardDto convertToCardDto(Card card) {
        return CardDto.builder()
            .id(card.getId())
            .bin(card.getBin())
            .lastFour(card.getLastFour())
            .cvv(card.getCvv())
            .expirationDate(card.getExpirationDate())
            .status(card.getStatus())
            .ballance(card.getBallance())
            .build();
}
}
