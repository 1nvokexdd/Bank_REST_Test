package com.example.bankcards.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferRequestDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserException.UserAlreadyExistException;
import com.example.bankcards.exception.UserException.UserNotFoundException;
import com.example.bankcards.exception.UserException.UserNotOwnsThisCardException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.DAO.CardServiceDAO;
import com.example.bankcards.service.Impl.UserServiceImpl;
import com.example.bankcards.util.CardGenerator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardServiceDAO cardService;

    @Mock
    private CardGenerator cardGenerator;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
       
        Long userId = 1L;
        User expectedUser = new User();
        expectedUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

       
        User result = userService.getUserById(userId);

     
        assertEquals(expectedUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
       
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void save_ShouldReturnSavedUser() {
       
        User userToSave = new User();
        User savedUser = new User();
        savedUser.setId(1L);
        when(userRepository.save(userToSave)).thenReturn(savedUser);

      
        User result = userService.save(userToSave);

      
        assertEquals(savedUser, result);
        verify(userRepository).save(userToSave);
    }

    @Test
    void create_ShouldSaveUser_WhenPhoneNumberIsUnique() {
        
        User newUser = new User();
        newUser.setPhoneNumber("1234567890");
        when(userRepository.existsByPhoneNumber("1234567890")).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(newUser);

     
        User result = userService.create(newUser);

        assertEquals(newUser, result);
        verify(userRepository).existsByPhoneNumber("1234567890");
        verify(userRepository).save(newUser);
    }

    @Test
    void create_ShouldThrowUserAlreadyExistException_WhenPhoneNumberExists() {
     
        User newUser = new User();
        newUser.setPhoneNumber("1234567890");
        when(userRepository.existsByPhoneNumber("1234567890")).thenReturn(true);

       
        assertThrows(UserAlreadyExistException.class, () -> userService.create(newUser));
        verify(userRepository).existsByPhoneNumber("1234567890");
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        
        String username = "testUser";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        
        User result = userService.getUserByUsername(username);

       
        assertEquals(expectedUser, result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getUserByUsername_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        
        String username = "nonExistingUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        
        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void userDetailsService_ShouldReturnUserDetailsService() {
       
        UserDetailsService result = userService.userDetailsService();

      
        assertNotNull(result);
    }

    @Test
    void getCurrentUser_ShouldReturnAuthenticatedUser() {
     
        String username = "currentUser";
        User expectedUser = new User();
        expectedUser.setUsername(username);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

      
        User result = userService.getCurrentUser();

     
        assertEquals(expectedUser, result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void UserCardsTransfer_ShouldTransferMoney_WhenCardsBelongToUser() {
       
        Long currentUserId = 1L;
        Long fromCardId = 1L;
        Long toCardId = 2L;
        BigDecimal sum = BigDecimal.valueOf(100);

        TransferRequestDTO transferDTO = new TransferRequestDTO(fromCardId, toCardId, sum);

        User currentUser = new User();
        currentUser.setId(currentUserId);
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(currentUser));
        when(cardService.checkUserCard(currentUserId, fromCardId)).thenReturn(true);
        when(cardService.checkUserCard(currentUserId, toCardId)).thenReturn(true);

     
        userService.UserCardsTransfer(transferDTO);

      
        verify(cardService).checkUserCard(currentUserId, fromCardId);
        verify(cardService).checkUserCard(currentUserId, toCardId);
        verify(cardService).moneyTransfer(fromCardId, toCardId, sum);
    }

    @Test
    void UserCardsTransfer_ShouldThrowUserNotOwnsThisCardException_WhenFromCardNotBelongsToUser() {
      
        Long currentUserId = 1L;
        Long fromCardId = 1L;
        Long toCardId = 2L;
        BigDecimal sum = BigDecimal.valueOf(100);

        TransferRequestDTO transferDTO = new TransferRequestDTO(fromCardId, toCardId, sum);

        User currentUser = new User();
        currentUser.setId(currentUserId);
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(currentUser));
        when(cardService.checkUserCard(currentUserId, fromCardId)).thenReturn(false);

        assertThrows(UserNotOwnsThisCardException.class, () -> userService.UserCardsTransfer(transferDTO));
        verify(cardService).checkUserCard(currentUserId, fromCardId);
        verify(cardService, never()).moneyTransfer(anyLong(), anyLong(), any());
    }

    @Test
    void getUserBallance_ShouldReturnBalance_WhenCardBelongsToUser() {
     
        Long currentUserId = 1L;
        Long cardId = 1L;
        BigDecimal expectedBalance = BigDecimal.valueOf(1000);

        User currentUser = new User();
        currentUser.setId(currentUserId);
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(currentUser));
        when(cardService.checkUserCard(currentUserId, cardId)).thenReturn(true);
        when(cardService.getBallance(cardId)).thenReturn(expectedBalance);

     
        BigDecimal result = userService.getUserBallance(cardId);

      
        assertEquals(expectedBalance, result);
        verify(cardService).checkUserCard(currentUserId, cardId);
        verify(cardService).getBallance(cardId);
    }

    @Test
    void getUserCards_ShouldReturnPageOfCardDtos() {
        
        Long currentUserId = 1L;
        int page = 0;
        int size = 10;
        
        User currentUser = new User();
        currentUser.setId(currentUserId);
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(currentUser));
        
        Page<CardDto> expectedPage = new PageImpl<>(List.of(new CardDto()));
        when(cardService.findUserCardsById(currentUserId, page, size)).thenReturn(expectedPage);

     
        Page<CardDto> result = userService.getUserCards(page, size);

      
        assertEquals(expectedPage, result);
        verify(cardService).findUserCardsById(currentUserId, page, size);
    }

    @Test
    void requestCardBlock_ShouldBlockCard_WhenCardBelongsToUser() {
        
        Long currentUserId = 1L;
        Long cardId = 1L;
        
        User currentUser = new User();
        currentUser.setId(currentUserId);
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(currentUser));
        when(cardService.checkUserCard(currentUserId, cardId)).thenReturn(true);

      
        userService.requestCardBlock(cardId);

        verify(cardService).checkUserCard(currentUserId, cardId);
        verify(cardService).userBlockCard(cardId);
    }

    @Test
    void getUsernameByPhoneNumber_ShouldReturnUsername_WhenUserExists() {
       
        String phoneNumber = "1234567890";
        String expectedUsername = "testUser";
        
        User user = new User();
        user.setUsername(expectedUsername);
        
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(user));

       
        String result = userService.getUsernameByPhoneNumber(phoneNumber);

       
        assertEquals(expectedUsername, result);
        verify(userRepository).findByPhoneNumber(phoneNumber);
    }

    @Test
    void delete_ShouldDeleteUser() {
        
        Long userId = 1L;
        User userToDelete = new User();
        userToDelete.setId(userId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));

     
        userService.delete(userId);

    
        verify(userRepository).findById(userId);
        verify(userRepository).delete(userToDelete);
    }

    @Test
    void getDecryptCardNumber_ShouldReturnDecryptedNumber_WhenCardBelongsToUser() {
       
        Long currentUserId = 1L;
        Long cardId = 1L;
        String encryptedNumber = "encrypted";
        String decryptedNumber = "1234567890123456";
        
        User currentUser = new User();
        currentUser.setId(currentUserId);
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(currentUser));
        
        Card card = new Card();
        card.setId(cardId);
        card.setOwner(currentUser);
        card.setEncryptedCardNumber(encryptedNumber);
        
        when(cardService.getCardById(cardId)).thenReturn(card);
        when(cardGenerator.decryptCardNumber(encryptedNumber)).thenReturn(decryptedNumber);

      
        String result = userService.getDecryptCardNumber(cardId);

        assertEquals(decryptedNumber, result);
        verify(cardService).getCardById(cardId);
        verify(cardGenerator).decryptCardNumber(encryptedNumber);
    }
}