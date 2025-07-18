package com.example.bankcards.service.Impl;


import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.example.bankcards.dto.AddBallanceRequestDTO;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.CARD_STATUS;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.ROLE;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardException.CardBlockException;
import com.example.bankcards.exception.CardException.CardCreateException;
import com.example.bankcards.exception.CardException.CardNotFoundException;
import com.example.bankcards.exception.UserException.UserNotFoundException;
import com.example.bankcards.service.DAO.AdminServiceDAO;
import com.example.bankcards.service.DAO.CardServiceDAO;
import com.example.bankcards.service.DAO.UserServiceDAO;
import com.example.bankcards.util.CardGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminServiceDAO{
    private final CardServiceDAO cardService;
    private final UserServiceDAO userService;
    private final CardGenerator cardGenerator;


     /**
     * Создает новую банковскую карту для указанного пользователя
     *
     * @param ownerId идентификатор пользователя-владельца карты
     * @return созданный объект карты
     * @throws CardCreateException если не получилось создать карту
     */
    @Override
    public Card createCard(Long ownerId){
        User owner = userService.getUserById(ownerId);
        Card card = cardGenerator.generateCard(owner);
    

        return cardService.save(card);
    };



    /**
     * Получает расшифрованный номер карты
     *
     * @param cardId идентификатор карты
     * @return расшифрованный номер карты в виде строки
     * @throws CardNotFoundException если карта не найдена 
     */
    @Override
    public String getDecryptCardNumber(Long cardId){
        Card card  = cardService.getCardById(cardId);
       
        return    cardGenerator.decryptCardNumber(card.getEncryptedCardNumber());
    }


     /**
     * Назначает пользователю роль администратора
     *
     * @param id идентификатор пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public void addAdmin(Long id){
        User user = userService.getUserById(id);
        user.setRole(ROLE.ROLE_ADMIN);

        userService.save(user);
    };



    /**
     * Блокирует указанную карту
     *
     * @param id идентификатор карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public void blockCard(Long id){
        cardService.block(id);
    };



    /**
     * Активирует указанную карту
     *
     * @param id идентификатор карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public void activateCard(Long id){
        cardService.activate(id);
    };


    /**
     * Удаляет указанную карту
     *
     * @param id идентификатор карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public void deleteCard(Long id){
        cardService.delete(id);
    };


    /**
     * Получает страницу со списком всех карт в системе
     *
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return страница с картами в формате DTO
     */
    @Override
    public Page<CardDto> getAllCards(int page , int size){
        return  cardService.getAllCards( page ,  size);
    };


     /**
     * Подтверждает блокировку карты (переводит из состояния PENDING_BLOCK в BLOCKED)
     *
     * @param cardId идентификатор карты
     * @throws CardBlockException если карта не в состоянии ожидания блокировки
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public void approveBlock(Long cardId){
        if (cardService.getCardById(cardId).getStatus() != CARD_STATUS.PENDING_BLOCK) {
            throw new CardBlockException(cardId);
        }

        cardService.block(cardId);
    };



    /**
     * Отклоняет запрос на блокировку карты (возвращает в активное состояние)
     *
     * @param cardId идентификатор карты
     * @throws CardBlockException если карта не в состоянии ожидания блокировки
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public void  rejectBlock(Long cardId){
    
        if (cardService.getCardById(cardId).getStatus() != CARD_STATUS.PENDING_BLOCK) {
            throw new CardBlockException(cardId);
        }

        cardService.activate(cardId);
    };


     /**
     * Получает страницу со списком карт, ожидающих блокировки
     *
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return страница с картами в формате DTO
     */
    @Override
    public Page<CardDto> getPendingBlockCards(int page , int size){
        return cardService.findByStatus(CARD_STATUS.PENDING_BLOCK,page, size);
    };


    /**
     * Удаляет пользователя и все связанные с ним карты
     *
     * @param userId идентификатор пользователя
     * @throws UserNotFoundException если карта не найдена
     */
    @Override
    public void deleteUser(Long userId){
        User user = userService.getUserById(userId);
        for (Card card : user.getCards()) {
            cardService.delete(card.getId());
        }

        userService.delete(userId);
   
    };


    /**
     * Пополняет баланс указанной карты
     *
     * @param dto DTO-объект с данными для пополнения (идентификатор карты и сумма)
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public void addBallance(AddBallanceRequestDTO dto){
        
        Card card = cardService.getCardById(dto.getCardId());
        card.setBallance(card.getBallance().add(dto.getSum()));

        cardService.save(card);
    };
}
