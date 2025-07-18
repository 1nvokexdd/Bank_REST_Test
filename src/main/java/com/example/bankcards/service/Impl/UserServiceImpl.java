package com.example.bankcards.service.Impl;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferRequestDTO;
import com.example.bankcards.entity.CARD_STATUS;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardException.CardBlockException;
import com.example.bankcards.exception.CardException.CardNotFoundException;
import com.example.bankcards.exception.CardException.CardRequestBlockException;
import com.example.bankcards.exception.CardException.CardTransferMoneyException;
import com.example.bankcards.exception.UserException.UserAlreadyExistException;
import com.example.bankcards.exception.UserException.UserNotFoundException;
import com.example.bankcards.exception.UserException.UserNotOwnsThisCardException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.DAO.CardServiceDAO;
import com.example.bankcards.service.DAO.UserServiceDAO;
import com.example.bankcards.util.CardGenerator;
import com.ulisesbocchio.jasyptspringboot.exception.DecryptionException;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserServiceDAO {
    private final UserRepository userRepository;
    private final CardServiceDAO cardService;
    private final CardGenerator cardGenerator;



     /**
     * Получает пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return найденный объект пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
    };



    /**
     * Сохраняет пользователя в базе данных
     *
     * @param user объект пользователя для сохранения
     * @return сохраненный объект пользователя
     */
    @Override
    public User save(User user){
        return userRepository.save(user);
    };



     /**
     * Создает нового пользователя
     *
     * @param user объект пользователя для создания
     * @return созданный объект пользователя
     * @throws UserAlreadyExistException если пользователь с таким номером телефона уже существует
     */
    @Override
    public User create(User user){
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new UserAlreadyExistException();
        }

        return save(user);
    };


     /**
     * Получает пользователя по имени пользователя (username)
     *
     * @param username имя пользователя для поиска
     * @return найденный объект пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException());
    };



     /**
     * Создает сервис для работы с UserDetails
     *
     * @return реализация UserDetailsService
     */
    @Override
    public UserDetailsService userDetailsService(){
        return this::getUserByUsername;
    };



    /**
     * Получает текущего аутентифицированного пользователя
     *
     * @return объект текущего пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public  User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return getUserByUsername(username);
    };



    /**
     * Выполняет перевод денег между картами текущего пользователя
     *
     * @param transferDTO DTO с данными для перевода (карта отправителя, карта получателя, сумма)
     * @throws UserNotOwnsThisCardException если пользователь не владеет одной из карт
     * @throws CardTransferMoneyException если возникла ошибка при переводе (см. CardServiceDAO.moneyTransfer)
     */
    @Override
    @Transactional
    public  void UserCardsTransfer(TransferRequestDTO transferDTO){
        Long currentUserId = getCurrentUser().getId();
        if (!cardService.checkUserCard(currentUserId, transferDTO.getFromCardId())) {
            throw new UserNotOwnsThisCardException(currentUserId , transferDTO.getFromCardId());
        }
         if (!cardService.checkUserCard(currentUserId, transferDTO.getToCardId())) {
            throw new UserNotOwnsThisCardException(currentUserId , transferDTO.getToCardId());
        }
        

        cardService.moneyTransfer(transferDTO.getFromCardId(), transferDTO.getToCardId(), transferDTO.getSum());
    };
    

    /**
     * Получает баланс карты текущего пользователя
     *
     * @param cardId идентификатор карты
     * @return текущий баланс карты
     * @throws UserNotOwnsThisCardException если карта не принадлежит текущему пользователю
     * @throws CardNotFoundException если карта не найдена
     */
    @Override
    public  BigDecimal getUserBallance(Long cardId){
        if (!cardService.checkUserCard( getCurrentUser().getId() ,cardId)) {
            throw new UserNotOwnsThisCardException(getCurrentUser().getId() , cardId);
        }

        return cardService.getBallance(cardId);
    };

   
    /**
     * Получает страницу с картами текущего пользователя
     *
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return страница с картами пользователя в формате DTO
     */
    @Override
    public Page<CardDto>getUserCards(int page , int size ){
        return cardService.findUserCardsById(getCurrentUser().getId(), page, size);
    };


    /**
     * Отправляет запрос на блокировку карты текущего пользователя
     *
     * @param cardId идентификатор карты
     * @throws UserNotOwnsThisCardException если карта не принадлежит текущему пользователю
     * @throws CardRequestBlockException если:
     *         - карта уже заблокирована
     *         - уже есть запрос на блокировку этой карты
     */
    @Override
    @Transactional
    public void requestCardBlock(Long cardId){
        if (!cardService.checkUserCard(getCurrentUser().getId(), cardId)) {
             throw new UserNotOwnsThisCardException(getCurrentUser().getId() , cardId);
        }
        if (cardService.getCardById(cardId).getStatus() == CARD_STATUS.BLOCKED) {
             throw new CardRequestBlockException(cardId , "Эта карта уже заблокирована");
        }

         if (cardService.getCardById(cardId).getStatus() == CARD_STATUS.PENDING_BLOCK) {
             throw new CardRequestBlockException(cardId , "Уже был запрос блокировки этой карты");
        }

      

        cardService.userBlockCard(cardId);
    };


    /**
     * Получает имя пользователя по номеру телефона
     *
     * @param phoneNumber номер телефона для поиска
     * @return имя пользователя (username)
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public String getUsernameByPhoneNumber(String phoneNumber){
        User user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UserNotFoundException());

        return user.getUsername();
    }


    /**
     * Удаляет пользователя
     *
     * @param userId идентификатор пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public void delete(Long userId){
        User user = getUserById(userId);

        userRepository.delete(user);
    };



    /**
     * Получает расшифрованный номер карты текущего пользователя
     *
     * @param cardId идентификатор карты
     * @return расшифрованный номер карты
     * @throws UserNotOwnsThisCardException если карта не принадлежит текущему пользователю
     * @throws CardNotFoundException если карта не найдена
     * @throws DecryptionException если возникла ошибка при расшифровке
     */
    @Override
    public String getDecryptCardNumber(Long cardId){
       
        Card card = cardService.getCardById(cardId);
        if (card.getOwner().getId() != getCurrentUser().getId()) {
            throw new UserNotOwnsThisCardException(getCurrentUser().getId(), cardId);
        }

        return cardGenerator.decryptCardNumber(card.getEncryptedCardNumber());
    };
 
}
