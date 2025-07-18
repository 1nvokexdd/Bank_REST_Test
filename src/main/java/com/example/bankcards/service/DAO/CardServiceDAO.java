package com.example.bankcards.service.DAO;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.CARD_STATUS;
import com.example.bankcards.entity.Card;


public interface CardServiceDAO {
    Card save(Card card);
    Card create(Card card);
    void block(Long cardId);
    BigDecimal getBallance(Long cardId);
    Card getCardById(Long  cartId);
    boolean checkUserCard(Long userId , Long cardId);
    void userBlockCard(Long cardId);
    Page<CardDto> findUserCardsById(Long userId ,  int page, int size);
    Page<Card> findWithPagingCards(Long userId , CARD_STATUS status, int page , int size);
    Page<CardDto> findByStatus(CARD_STATUS status , int page , int size);
    void moneyTransfer(Long fromId , Long toId ,  BigDecimal sum);
    void delete(Long id);
    void activate(Long id);
    Page<CardDto>getAllCards(int page , int size);
}
