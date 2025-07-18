package com.example.bankcards.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardException.CardNotFoundException;
import com.example.bankcards.service.DAO.CardServiceDAO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardsController {
    private final CardServiceDAO cardService;




    /**
     * Возращает замискированный номер карты 
     * 
     * @param cardId ID карты для получения ее номера 
     * @return ResponseEntity с замаскированным номером карты и статусом 200 (OK)
     * @throws CardNotFoundException если карта с ID cardId не найдена
     */

    @GetMapping("/{cardId}")
    public ResponseEntity<String> getCardNUmber(@PathVariable(name = "cardId") Long cardId) {
        Card card = cardService.getCardById(cardId);
        return ResponseEntity.status(200).body(card.getMaskedNumber());
    }
    
}
