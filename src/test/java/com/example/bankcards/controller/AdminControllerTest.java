package com.example.bankcards.controller;

import com.example.bankcards.dto.AddBallanceRequestDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.DAO.AdminServiceDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;




class AdminControllerUnitTest {

    @Mock
    private AdminServiceDAO adminService;

    @InjectMocks
    private AdminController adminController;  

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDecryptCardNumber_shouldReturnDecryptedNumber() {
        Long cardId = 1L;
        String decrypted = "1234 5678 9012 3456";

        when(adminService.getDecryptCardNumber(cardId)).thenReturn(decrypted);

        ResponseEntity<String> response = adminController.getDecryptCardNumber(cardId);

        assertEquals(200, response.getStatusCode());
        assertEquals(decrypted, response.getBody());

        verify(adminService).getDecryptCardNumber(cardId);
    }

  



    @Test
    void createCard_shouldReturnCreatedCard() {
        Long ownerId = 123L;
        Card card = new Card();
        card.setId(1L);

        when(adminService.createCard(ownerId)).thenReturn(card);

        ResponseEntity<Card> response = adminController.createCard(ownerId);

        assertEquals(201, response.getStatusCode());
        assertEquals(card, response.getBody());

        verify(adminService).createCard(ownerId);
    }

    @Test
    void deleteCard_shouldReturnNoContentWithMessage() {
        Long cardId = 1L;

        doNothing().when(adminService).deleteCard(cardId);

        ResponseEntity<String> response = adminController.deleteCard(cardId);

        assertEquals(204, response.getStatusCode());
        assertEquals("Card " + cardId + " deleted", response.getBody());

        verify(adminService).deleteCard(cardId);
    }

    @Test
    void activateCard_shouldReturnActivatedMessage() {
        Long cardId = 2L;

        doNothing().when(adminService).activateCard(cardId);

        ResponseEntity<String> response = adminController.activateCard(cardId);

        assertEquals(200,response.getStatusCode());
        assertEquals("Card " + cardId + " activated", response.getBody());

        verify(adminService).activateCard(cardId);
    }

    @Test
    void deleteUser_shouldReturnOk() {
        Long userId = 5L;

        doNothing().when(adminService).deleteUser(userId);

        ResponseEntity<Void> response = adminController.deleteUser(userId);

        assertEquals(200, response.getStatusCode());

        verify(adminService).deleteUser(userId);
    }

    @Test
    void addMoneyOnCard_shouldReturnOk() {
        AddBallanceRequestDTO dto = new AddBallanceRequestDTO();
        dto.setCardId(1L);
        dto.setSum(new BigDecimal("500.00"));

        doNothing().when(adminService).addBallance(dto);

        ResponseEntity<Void> response = adminController.addMoneyOnCard(dto);

        assertEquals(200, response.getStatusCode());

        verify(adminService).addBallance(dto);
    }
}
