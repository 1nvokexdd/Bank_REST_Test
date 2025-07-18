package com.example.bankcards.service.DAO;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.TransferRequestDTO;
import com.example.bankcards.entity.User;



public interface UserServiceDAO {
    void UserCardsTransfer(TransferRequestDTO transferDTO);
    User getUserById(Long id);
    User save(User user);
    User create(User user);
    User getUserByUsername(String username);
    UserDetailsService userDetailsService();
    User getCurrentUser();
    BigDecimal getUserBallance(Long cardId);
    String getUsernameByPhoneNumber(String phoneNumber);
    Page<CardDto>getUserCards(int page , int size);
    void requestCardBlock(Long cardId);
    void delete(Long userId);
    String getDecryptCardNumber(Long cardId);
}
