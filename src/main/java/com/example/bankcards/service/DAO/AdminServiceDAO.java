package com.example.bankcards.service.DAO;


import org.springframework.data.domain.Page;
import com.example.bankcards.dto.AddBallanceRequestDTO;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;



public interface AdminServiceDAO {
    Card createCard(Long ownerId);
    void addAdmin(Long id);
    void blockCard(Long id);
    void activateCard(Long id);
    void deleteCard(Long id);
    Page<CardDto>  getAllCards(int page , int size);
    void approveBlock(Long cardId);
    void  rejectBlock(Long cardId);
    Page<CardDto> getPendingBlockCards(int page , int size);
    String getDecryptCardNumber(Long cardId);
    void deleteUser(Long userId);
    void addBallance(AddBallanceRequestDTO dto);
}
