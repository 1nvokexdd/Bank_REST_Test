package com.example.bankcards.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.bankcards.dto.AddBallanceRequestDTO;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.DAO.AdminServiceDAO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminServiceDAO adminService;
    


     /**
     * Создает новую карту для указанного пользователя
     * 
     * @param ownerId ID пользователя-владельца карты
     * @return ResponseEntity с созданным объектом Card и статусом 201 (Created)
     */

    @PostMapping("/create-card/{ownerId}")
    public ResponseEntity<Card> createCard(@PathVariable(name = "ownerId") Long ownerId) {
        Card card =  adminService.createCard(ownerId);
        
        return ResponseEntity.status(201).body(card);
    }


    /**
     * Активирует карту по её ID
     * 
     * @param cardId ID карты для активации
     * @return ResponseEntity с сообщением об успешной активации и статусом 200 (OK)
     */

    @PostMapping("/activate-card/{cardId}")
    public ResponseEntity<String> activateCard(@PathVariable(name = "cardId")Long cardId) {
        
        adminService.activateCard(cardId);

        return ResponseEntity.status(200).body("Card " + cardId + " activated");
    
    }

    /**
     * Блокирует карту по её ID
     * 
     * @param cardId ID карты для блокировки
     * @return ResponseEntity с сообщением об успешной блокировке и статусом 200 (OK)
     */
    
    @PostMapping("/block-card/{cardId}")
    public ResponseEntity<String> blockCard(@PathVariable(name = "cardId")Long cardId) {
        
        adminService.blockCard(cardId);

        return ResponseEntity.status(200).body("Card " + cardId + " blocked");
    
    }
    

      /**
     * Назначает пользователю роль ADMIN
     * 
     * @param userId ID пользователя для назначения прав администратора
     * @return ResponseEntity с сообщением о успешном назначении роли и статусом 200 (OK)
     */
    
    @PostMapping("/add-admin/{userId}")
    public ResponseEntity<String> giveAdminRole(@PathVariable(name = "userId") Long userId) {
        
        adminService.addAdmin(userId);

        return ResponseEntity.status(200).body("User " + userId + " received ADMIN role");
    }
    


    /**
     * Подтверждает запрос на блокировку карты
     * 
     * @param cardId ID карты для подтверждения блокировки
     * @return ResponseEntity со статусом 200 (OK) без тела ответа
     */
    
    @PostMapping("/approve-block/{cardId}")
    public ResponseEntity<Void> approveBlock(@PathVariable(name = "cardId")Long cardId) {
        
        adminService.approveBlock(cardId);

        return ResponseEntity.status(200).build();
    }
    
    

    /**
     * Отклоняет запрос на блокировку карты
     * 
     * @param cardId ID карты для отклонения блокировки
     * @return ResponseEntity со статусом 200 (OK) без тела ответа
     */
    
    @PostMapping("/reject/{cardId}")
    public ResponseEntity<Void>  rejectBlock(@PathVariable(name = "cardId")Long cardId) {
        
        adminService.rejectBlock(cardId);
        
        return ResponseEntity.status(200).build();
    }
    

  

    
    /**
     * Пополняет баланс карты
     * 
     * @param dto DTO с данными для пополнения баланса
     * @return ResponseEntity со статусом 200 (OK) без тела ответа
     */
    
    @PostMapping("/add-ballance")
    public ResponseEntity<Void>  addMoneyOnCard(@RequestBody @Valid  AddBallanceRequestDTO dto) {
        
        adminService.addBallance(dto);
        
         return ResponseEntity.status(200).build();
    }
    
    

    /**
     * Удаляет карту по её ID
     * 
     * @param cardId ID карты для удаления
     * @return ResponseEntity с сообщением об успешном удалении и статусом 204 (No Content)
     */
    
    @DeleteMapping("/delete-card/{cardId}")
    public ResponseEntity<String> deleteCard(@PathVariable(name = "cardId") Long cardId) {
        
        adminService.deleteCard(cardId);

        return ResponseEntity.status(204).body("Card " + cardId + " deleted");
    }



    /**
     * Удаляет пользователя по его ID
     * 
     * @param userId ID пользователя для удаления
     * @return ResponseEntity со статусом 200 (OK) без тела ответа
     */
    
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void>  deleteUser(@PathVariable(name = "userId")Long userId) {
        
        adminService.deleteUser(userId);
        
        return ResponseEntity.status(200).build();
    }


    /**
     * Получает список всех карт с пагинацией
     * 
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return ResponseEntity с Page объектов CardDto и статусом 200 (OK)
     */
    
    @GetMapping("/get-all-cards")
    public ResponseEntity<Page<CardDto>> getAllCards(
            @Parameter(description = "Номер страницы") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") 
            @RequestParam(defaultValue = "10") int size) {
        return  ResponseEntity.status(200).body(adminService.getAllCards(page, size));
    }



    /**
     * Получает список карт, ожидающих блокировки, с пагинацией
     * 
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return ResponseEntity с Page объектов CardDto и статусом 200 (OK)
     */
    
    @GetMapping("/get-pending-block-cards")
    public ResponseEntity<Page<CardDto>> getPendingBlockCards(
            @Parameter(description = "Номер страницы") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") 
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(200).body(adminService.getPendingBlockCards(page, size));
    }

    
    /**
     * Получает полный расшифрованный номер карты
     * 
     * @param cardId ID карты для получения номера
     * @return ResponseEntity с расшифрованным номером карты и статусом 200 (OK)
     * @throws NotFoundException если карта не найдена
     */
   
    @GetMapping("/get-decrypt-card-number/{cardId}")
    public ResponseEntity<String> getDecryptCardNumber(@PathVariable(name = "cardId") Long cardId) {
        return ResponseEntity.status(200).body(adminService.getDecryptCardNumber(cardId));
    }
    
}
