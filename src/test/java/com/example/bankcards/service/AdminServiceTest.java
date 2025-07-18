package com.example.bankcards.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import com.example.bankcards.dto.AddBallanceRequestDTO;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.CARD_STATUS;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardException.CardBlockException;
import com.example.bankcards.service.DAO.CardServiceDAO;
import com.example.bankcards.service.DAO.UserServiceDAO;
import com.example.bankcards.service.Impl.AdminServiceImpl;
import com.example.bankcards.util.CardGenerator;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private CardServiceDAO cardService;

    @Mock
    private UserServiceDAO userService;

    @Mock
    private CardGenerator cardGenerator;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void createCard_ShouldGenerateAndSaveCardForUser() {
        
        Long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        
        Card generatedCard = new Card();
        generatedCard.setOwner(owner);
        
        when(userService.getUserById(ownerId)).thenReturn(owner);
        when(cardGenerator.generateCard(owner)).thenReturn(generatedCard);
        when(cardService.save(generatedCard)).thenReturn(generatedCard);

       
        Card result = adminService.createCard(ownerId);

        
        assertEquals(generatedCard, result);
        verify(userService).getUserById(ownerId);
        verify(cardGenerator).generateCard(owner);
        verify(cardService).save(generatedCard);
    }

    @Test
    void getDecryptCardNumber_ShouldReturnDecryptedNumber() {
        
        Long cardId = 1L;
        String encryptedNumber = "encrypted123";
        String decryptedNumber = "1234567890123456";
        
        Card card = new Card();
        card.setEncryptedCardNumber(encryptedNumber);
        
        when(cardService.getCardById(cardId)).thenReturn(card);
        when(cardGenerator.decryptCardNumber(encryptedNumber)).thenReturn(decryptedNumber);

      
        String result = adminService.getDecryptCardNumber(cardId);

        
        assertEquals(decryptedNumber, result);
        verify(cardService).getCardById(cardId);
        verify(cardGenerator).decryptCardNumber(encryptedNumber);
    }

   

    @Test
    void blockCard_ShouldCallCardServiceBlock() {
        
        Long cardId = 1L;
        doNothing().when(cardService).block(cardId);

      
        adminService.blockCard(cardId);

       
        verify(cardService).block(cardId);
    }

    @Test
    void activateCard_ShouldCallCardServiceActivate() {
        
        Long cardId = 1L;
        doNothing().when(cardService).activate(cardId);

       
        adminService.activateCard(cardId);

       
        verify(cardService).activate(cardId);
    }

    @Test
    void deleteCard_ShouldCallCardServiceDelete() {
     
        Long cardId = 1L;
        doNothing().when(cardService).delete(cardId);

        
        adminService.deleteCard(cardId);

     
        verify(cardService).delete(cardId);
    }

    @Test
    void getAllCards_ShouldReturnPageOfCards() {
        
        int page = 0;
        int size = 10;
        Page<CardDto> expectedPage = new PageImpl<>(Collections.emptyList());
        
        when(cardService.getAllCards(page, size)).thenReturn(expectedPage);

       
        Page<CardDto> result = adminService.getAllCards(page, size);

        
        assertEquals(expectedPage, result);
        verify(cardService).getAllCards(page, size);
    }

    @Test
    void approveBlock_ShouldBlockCard_WhenStatusIsPendingBlock() {
       
        Long cardId = 1L;
        Card card = new Card();
        card.setStatus(CARD_STATUS.PENDING_BLOCK);
        
        when(cardService.getCardById(cardId)).thenReturn(card);
        doNothing().when(cardService).block(cardId);

        adminService.approveBlock(cardId);

      
        verify(cardService).getCardById(cardId);
        verify(cardService).block(cardId);
    }

    @Test
    void approveBlock_ShouldThrowException_WhenStatusIsNotPendingBlock() {
      
        Long cardId = 1L;
        Card card = new Card();
        card.setStatus(CARD_STATUS.ACTIVE);
        
        when(cardService.getCardById(cardId)).thenReturn(card);

        
        assertThrows(CardBlockException.class, () -> adminService.approveBlock(cardId));
        verify(cardService).getCardById(cardId);
        verify(cardService, never()).block(anyLong());
    }

    @Test
    void rejectBlock_ShouldActivateCard_WhenStatusIsPendingBlock() {
       
        Long cardId = 1L;
        Card card = new Card();
        card.setStatus(CARD_STATUS.PENDING_BLOCK);
        
        when(cardService.getCardById(cardId)).thenReturn(card);
        doNothing().when(cardService).activate(cardId);

     
        adminService.rejectBlock(cardId);

     
        verify(cardService).getCardById(cardId);
        verify(cardService).activate(cardId);
    }

    @Test
    void rejectBlock_ShouldThrowException_WhenStatusIsNotPendingBlock() {
      
        Long cardId = 1L;
        Card card = new Card();
        card.setStatus(CARD_STATUS.BLOCKED);
        
        when(cardService.getCardById(cardId)).thenReturn(card);

        
        assertThrows(CardBlockException.class, () -> adminService.rejectBlock(cardId));
        verify(cardService).getCardById(cardId);
        verify(cardService, never()).activate(anyLong());
    }

    @Test
    void getPendingBlockCards_ShouldReturnFilteredPage() {
     
        int page = 0;
        int size = 10;
        Page<CardDto> expectedPage = new PageImpl<>(Collections.emptyList());
        
        when(cardService.findByStatus(CARD_STATUS.PENDING_BLOCK, page, size)).thenReturn(expectedPage);

     
        Page<CardDto> result = adminService.getPendingBlockCards(page, size);

      
        assertEquals(expectedPage, result);
        verify(cardService).findByStatus(CARD_STATUS.PENDING_BLOCK, page, size);
    }

    @Test
    void deleteUser_ShouldDeleteUserAndAllCards() {
        
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        
        Card card1 = new Card();
        card1.setId(1L);
        Card card2 = new Card();
        card2.setId(2L);
        user.setCards(List.of(card1, card2));
        
        when(userService.getUserById(userId)).thenReturn(user);
        doNothing().when(cardService).delete(1L);
        doNothing().when(cardService).delete(2L);
        doNothing().when(userService).delete(userId);

    
        adminService.deleteUser(userId);

       
        verify(userService).getUserById(userId);
        verify(cardService).delete(1L);
        verify(cardService).delete(2L);
        verify(userService).delete(userId);
    }

    @Test
    void addBalance_ShouldIncreaseCardBalance() {
        
        Long cardId = 1L;
        BigDecimal initialBalance = BigDecimal.valueOf(100);
        BigDecimal addedAmount = BigDecimal.valueOf(50);
        
        AddBallanceRequestDTO dto = new AddBallanceRequestDTO();
        dto.setCardId(cardId);
        dto.setSum(addedAmount);
        
        Card card = new Card();
        card.setBallance(initialBalance);
        
        when(cardService.getCardById(cardId)).thenReturn(card);
        when(cardService.save(card)).thenReturn(card);

    
        adminService.addBallance(dto);

      
        assertEquals(initialBalance.add(addedAmount), card.getBallance());
        verify(cardService).getCardById(cardId);
        verify(cardService).save(card);
    }
}