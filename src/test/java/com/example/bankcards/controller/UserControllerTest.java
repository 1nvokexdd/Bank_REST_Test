package com.example.bankcards.controller;

import com.example.bankcards.dto.*;
import com.example.bankcards.service.DAO.UserServiceDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



class UserControllerTest {

    @Mock
    private com.example.bankcards.security.AuthenticationService authenticationService;

    @Mock
    private UserServiceDAO userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBallance_shouldReturnBalance() {
        Long cardId = 10L;
        BigDecimal balance = new BigDecimal("1234.56");

        when(userService.getUserBallance(cardId)).thenReturn(balance);

        ResponseEntity<BigDecimal> response = userController.getBallance(cardId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(balance, response.getBody());

        verify(userService).getUserBallance(cardId);
    }

    @Test
    void getUserCards_shouldReturnPageOfCards() {
        int page = 0;
        int size = 5;
      

        Page<CardDto> cardsPage = new PageImpl<>(List.of(new CardDto()));

        when(userService.getUserCards(page, size)).thenReturn(cardsPage);

        ResponseEntity<Page<CardDto>> response = userController.getUserCards(page,size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cardsPage, response.getBody());

        verify(userService).getUserCards(page, size);
    }

    @Test
    void transferMoneyBetweenCards_shouldReturnOk() {
        TransferRequestDTO dto = new TransferRequestDTO();

        doNothing().when(userService).UserCardsTransfer(dto);

        ResponseEntity<Void> response = userController.transferMoneyBetweenCards(dto);

         assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(userService).UserCardsTransfer(dto);
    }

    @Test
    void blockCardRequest_shouldReturnOk() {
        Long cardId = 15L;

        doNothing().when(userService).requestCardBlock(cardId);

        ResponseEntity<Void> response = userController.blockCardRequest(cardId);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(userService).requestCardBlock(cardId);
    }

    @Test
    void signUp_shouldReturnJwtResponse() {
        SignUpRequest request = new SignUpRequest();
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("token123");

        when(authenticationService.signUp(request)).thenReturn(jwtResponse);

        ResponseEntity<JwtAuthenticationResponse> response = userController.signUp(request);

        assertEquals(jwtResponse, response.getBody());

        verify(authenticationService).signUp(request);
    }

    @Test
    void signIn_shouldReturnJwtResponse() {
        SignInRequest request = new SignInRequest();
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("token456");

        when(authenticationService.signIn(request)).thenReturn(jwtResponse);

        ResponseEntity<JwtAuthenticationResponse> response = userController.signIn(request);

        assertEquals(jwtResponse, response.getBody());

        verify(authenticationService).signIn(request);
    }

    @Test
    void getDecryptCardNumber_shouldReturnDecryptedNumber() {
        Long cardId = 20L;
        String decrypted = "1234 5678 9012 3456";

        when(userService.getDecryptCardNumber(cardId)).thenReturn(decrypted);

        ResponseEntity<String> response = userController.getDecryptCardNumber(cardId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(decrypted, response.getBody());

        verify(userService).getDecryptCardNumber(cardId);
    }
}