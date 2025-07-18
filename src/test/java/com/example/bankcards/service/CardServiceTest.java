package com.example.bankcards.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.CARD_STATUS;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardException.CardNotFoundException;
import com.example.bankcards.exception.CardException.CardTransferMoneyException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.Impl.CardServiceImpl;



@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository repository;

    @InjectMocks
    private CardServiceImpl cardService;

    @Test
    void save_ShouldReturnSavedCard() {
        
        Card cardToSave = new Card();
        Card savedCard = new Card();
        savedCard.setId(1L);
        when(repository.save(cardToSave)).thenReturn(savedCard);

        Card result = cardService.save(cardToSave);

        assertEquals(savedCard, result);
        verify(repository).save(cardToSave);
    }

    @Test
    void create_ShouldCallSave() {
      
        Card cardToCreate = new Card();
        Card createdCard = new Card();
        createdCard.setId(1L);
        when(repository.save(cardToCreate)).thenReturn(createdCard);

     
        Card result = cardService.create(cardToCreate);

      
        assertEquals(createdCard, result);
        verify(repository).save(cardToCreate);
    }

    @Test
    void getCardById_ShouldReturnCard_WhenCardExists() {
     
        Long cardId = 1L;
        Card expectedCard = new Card();
        expectedCard.setId(cardId);
        when(repository.findById(cardId)).thenReturn(Optional.of(expectedCard));

      
        Card result = cardService.getCardById(cardId);

        assertEquals(expectedCard, result);
        verify(repository).findById(cardId);
    }

    @Test
    void getCardById_ShouldThrowCardNotFoundException_WhenCardDoesNotExist() {
      
        Long cardId = 1L;
        when(repository.findById(cardId)).thenReturn(Optional.empty());

        
        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(cardId));
        verify(repository).findById(cardId);
    }

    @Test
    void getBalance_ShouldReturnBalance() {
      
        Long cardId = 1L;
        BigDecimal expectedBalance = BigDecimal.valueOf(1000);
        Card card = new Card();
        card.setBallance(expectedBalance);
        when(repository.findById(cardId)).thenReturn(Optional.of(card));

        BigDecimal result = cardService.getBallance(cardId);

        assertEquals(expectedBalance, result);
        verify(repository).findById(cardId);
    }

    @Test
    void checkUserCard_ShouldReturnTrue_WhenUserOwnsCard() {
    
        Long userId = 1L;
        Long cardId = 1L;
        User owner = new User();
        owner.setId(userId);
        Card card = new Card();
        card.setOwner(owner);
        when(repository.findById(cardId)).thenReturn(Optional.of(card));

    
        boolean result = cardService.checkUserCard(userId, cardId);

        assertTrue(result);
        verify(repository).findById(cardId);
    }

    @Test
    void checkUserCard_ShouldReturnFalse_WhenUserDoesNotOwnCard() {
        
        Long userId = 1L;
        Long cardId = 1L;
        User owner = new User();
        owner.setId(2L); 
        Card card = new Card();
        card.setOwner(owner);
        when(repository.findById(cardId)).thenReturn(Optional.of(card));

     
        boolean result = cardService.checkUserCard(userId, cardId);

        assertFalse(result);
        verify(repository).findById(cardId);
    }

    @Test
    void userBlockCard_ShouldSetStatusToPendingBlock() {
      
        Long cardId = 1L;
        Card card = new Card();
        card.setStatus(CARD_STATUS.ACTIVE);
        when(repository.findById(cardId)).thenReturn(Optional.of(card));
        when(repository.save(card)).thenReturn(card);

        cardService.userBlockCard(cardId);

        assertEquals(CARD_STATUS.PENDING_BLOCK, card.getStatus());
        verify(repository).findById(cardId);
        verify(repository).save(card);
    }

    @Test
    void findUserCardsById_ShouldReturnPageOfCardDtos() {
  
        Long userId = 1L;
        int page = 0;
        int size = 10;
        
        Card card = new Card();
        card.setId(1L);
        card.setBin("123456");
        card.setLastFour("7890");
        card.setCvv("123");
        card.setExpirationDate(LocalDate.now().plusYears(1));
        card.setStatus(CARD_STATUS.ACTIVE);
        card.setBallance(BigDecimal.valueOf(1000));
        
        Page<Card> cardPage = new PageImpl<>(Collections.singletonList(card));
        when(repository.findByOwnerId(userId, PageRequest.of(page, size, Sort.by("id").descending())))
            .thenReturn(cardPage);

  
        Page<CardDto> result = cardService.findUserCardsById(userId, page, size);

        assertEquals(1, result.getTotalElements());
        CardDto dto = result.getContent().get(0);
        assertEquals(card.getId(), dto.getId());
        assertEquals(card.getBin(), dto.getBin());
        assertEquals(card.getLastFour(), dto.getLastFour());
        assertEquals(card.getCvv(), dto.getCvv());
        assertEquals(card.getExpirationDate(), dto.getExpirationDate());
        assertEquals(card.getStatus(), dto.getStatus());
        assertEquals(card.getBallance(), dto.getBallance());
        
        verify(repository).findByOwnerId(userId, PageRequest.of(page, size, Sort.by("id").descending()));
    }

    @Test
    void findWithPagingCards_ShouldReturnFilteredPage() {
 
        Long userId = 1L;
        CARD_STATUS status = CARD_STATUS.ACTIVE;
        int page = 0;
        int size = 10;
        
        Page<Card> expectedPage = new PageImpl<>(Collections.emptyList());
        when(repository.findByOwnerIdAndStatus(userId, status, 
            PageRequest.of(page, size, Sort.by("id").descending())))
            .thenReturn(expectedPage);

        Page<Card> result = cardService.findWithPagingCards(userId, status, page, size);

        assertEquals(expectedPage, result);
        verify(repository).findByOwnerIdAndStatus(userId, status, 
            PageRequest.of(page, size, Sort.by("id").descending()));
    }

    @Test
    void moneyTransfer_ShouldTransferFunds_WhenConditionsAreMet() {
      
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal sum = BigDecimal.valueOf(100);
        
        Card fromCard = new Card();
        fromCard.setId(fromId);
        fromCard.setBallance(BigDecimal.valueOf(500));
        fromCard.setStatus(CARD_STATUS.ACTIVE);
        
        Card toCard = new Card();
        toCard.setId(toId);
        toCard.setBallance(BigDecimal.valueOf(200));
        toCard.setStatus(CARD_STATUS.ACTIVE);
        
        when(repository.findById(fromId)).thenReturn(Optional.of(fromCard));
        when(repository.findById(toId)).thenReturn(Optional.of(toCard));
        when(repository.save(fromCard)).thenReturn(fromCard);
        when(repository.save(toCard)).thenReturn(toCard);

  
        cardService.moneyTransfer(fromId, toId, sum);

        assertEquals(BigDecimal.valueOf(400), fromCard.getBallance());
        assertEquals(BigDecimal.valueOf(300), toCard.getBallance());
        
        verify(repository).findById(fromId);
        verify(repository).findById(toId);
        verify(repository).save(fromCard);
        verify(repository).save(toCard);
    }

    @Test
    void moneyTransfer_ShouldThrowException_WhenInsufficientFunds() {
    
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal sum = BigDecimal.valueOf(600);
        
        Card fromCard = new Card();
        fromCard.setId(fromId);
        fromCard.setBallance(BigDecimal.valueOf(500));
        fromCard.setStatus(CARD_STATUS.ACTIVE);
        
        Card toCard = new Card();
        toCard.setId(toId);
        toCard.setBallance(BigDecimal.valueOf(200));
        toCard.setStatus(CARD_STATUS.ACTIVE);
        
        when(repository.findById(fromId)).thenReturn(Optional.of(fromCard));
        when(repository.findById(toId)).thenReturn(Optional.of(toCard));

     
        assertThrows(CardTransferMoneyException.class, 
            () -> cardService.moneyTransfer(fromId, toId, sum));
        
        verify(repository, never()).save(any());
    }

    @Test
    void moneyTransfer_ShouldThrowException_WhenFromCardNotActive() {
    
        Long fromId = 1L;
        Long toId = 2L;
        BigDecimal sum = BigDecimal.valueOf(100);
        
        Card fromCard = new Card();
        fromCard.setId(fromId);
        fromCard.setBallance(BigDecimal.valueOf(500));
        fromCard.setStatus(CARD_STATUS.BLOCKED);
        
        Card toCard = new Card();
        toCard.setId(toId);
        toCard.setBallance(BigDecimal.valueOf(200));
        toCard.setStatus(CARD_STATUS.ACTIVE);
        
        when(repository.findById(fromId)).thenReturn(Optional.of(fromCard));
        when(repository.findById(toId)).thenReturn(Optional.of(toCard));

      
        assertThrows(CardTransferMoneyException.class, 
            () -> cardService.moneyTransfer(fromId, toId, sum));
        
        verify(repository, never()).save(any());
    }

    @Test
    void block_ShouldSetStatusToBlocked() {
      
        Long cardId = 1L;
        Card card = new Card();
        card.setStatus(CARD_STATUS.ACTIVE);
        when(repository.findById(cardId)).thenReturn(Optional.of(card));
        when(repository.save(card)).thenReturn(card);

   
        cardService.block(cardId);

     
        assertEquals(CARD_STATUS.BLOCKED, card.getStatus());
        verify(repository).findById(cardId);
        verify(repository).save(card);
    }

    @Test
    void delete_ShouldDeleteCard_WhenCardExists() {
     
        Long cardId = 1L;
        when(repository.existsById(cardId)).thenReturn(true);

    
        cardService.delete(cardId);

   
        verify(repository).existsById(cardId);
        verify(repository).deleteById(cardId);
    }

    @Test
    void delete_ShouldThrowException_WhenCardDoesNotExist() {
     
        Long cardId = 1L;
        when(repository.existsById(cardId)).thenReturn(false);

       
        assertThrows(CardNotFoundException.class, () -> cardService.delete(cardId));
        verify(repository).existsById(cardId);
        
    }

    @Test
    void activate_ShouldSetStatusToActive() {
        
        Long cardId = 1L;
        Card card = new Card();
        card.setStatus(CARD_STATUS.BLOCKED);
        when(repository.findById(cardId)).thenReturn(Optional.of(card));
        when(repository.save(card)).thenReturn(card);

     
        cardService.activate(cardId);

     
        assertEquals(CARD_STATUS.ACTIVE, card.getStatus());
        verify(repository).findById(cardId);
        verify(repository).save(card);
    }

    @Test
    void findByStatus_ShouldReturnFilteredPage() {
        // Arrange
        CARD_STATUS status = CARD_STATUS.ACTIVE;
        int page = 0;
        int size = 10;
        
        Card card = new Card();
        card.setId(1L);
        card.setStatus(status);
        
        Page<Card> cardPage = new PageImpl<>(Collections.singletonList(card));
        when(repository.findByStatus(status, PageRequest.of(page, size, Sort.by("id").descending())))
            .thenReturn(cardPage);

   
        Page<CardDto> result = cardService.findByStatus(status, page, size);

    
        assertEquals(1, result.getTotalElements());
        assertEquals(status, result.getContent().get(0).getStatus());
        verify(repository).findByStatus(status, PageRequest.of(page, size, Sort.by("id").descending()));
    }

    @Test
    void getAllCards_ShouldReturnPageOfCards() {
     
        int page = 0;
        int size = 10;
        
        Page<Card> expectedPage = new PageImpl<>(Collections.emptyList());
        when(repository.findAll(PageRequest.of(page, size, Sort.by("id").descending())))
            .thenReturn(expectedPage);

        Page<CardDto> result = cardService.getAllCards(page, size);

        assertEquals(expectedPage, result);
        verify(repository).findAll(PageRequest.of(page, size, Sort.by("id").descending()));
    }
}