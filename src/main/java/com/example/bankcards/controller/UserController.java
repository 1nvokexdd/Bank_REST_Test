package com.example.bankcards.controller;


import org.springframework.web.bind.annotation.RestController;
import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.JwtAuthenticationResponse;
import com.example.bankcards.dto.SignInRequest;
import com.example.bankcards.dto.SignUpRequest;
import com.example.bankcards.dto.TransferRequestDTO;
import com.example.bankcards.exception.CardException.CardNotFoundException;
import com.example.bankcards.exception.UserException.UserNotOwnsThisCardException;
import com.example.bankcards.security.AuthenticationService;
import com.example.bankcards.service.DAO.UserServiceDAO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;
    private final UserServiceDAO userService;

  
    /**
     * Регистрирует нового пользователя в системе
     *
     * @param request DTO с данными для регистрации (логин, пароль и др.)
     * @return ResponseEntity с JWT-токеном аутентификации и статусом 200 (OK)
     * @throws BadRequestException если пользователь с таким логином уже существует
     */
   
    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody @Valid SignUpRequest request) {
        return  ResponseEntity.ok().body(authenticationService.signUp(request));
    }


      /**
     * Аутентифицирует пользователя в системе
     *
     * @param request DTO с учетными данными (логин и пароль)
     * @return ResponseEntity с JWT-токеном аутентификации и статусом 200 (OK)
     * @throws UnauthorizedException если неверные учетные данные
     */

    @PostMapping("/sign-in") 
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.ok().body(authenticationService.signIn(request)) ;
    }


        /**
     * Выполняет перевод денежных средств между картами текущего пользователя
     *
     * @param dto DTO с данными перевода (номера карт, сумма)
     * @return ResponseEntity со статусом 200 (OK) без тела ответа
     * @throws InsufficientFundsException если недостаточно средств на карте-источнике
     * @throws CardNotFoundException если одна из карт не найдена
     */
 
    @PostMapping("/transfer-between-cards")
    public ResponseEntity<Void> transferMoneyBetweenCards(@RequestBody @Valid  TransferRequestDTO dto) {
        userService.UserCardsTransfer(dto);
       return ResponseEntity.status(200).build();
    }
    


        /**
     * Отправляет запрос на блокировку указанной карты
     *
     * @param cardId ID карты для блокировки
     * @return ResponseEntity со статусом 200 (OK) без тела ответа
     * @throws  CardNotFoundException если карта не найдена
     * @throws UserNotOwnsThisCardException если  карта не принадлежит текущему пользователю
     */

    @PostMapping("/block-card-request/{cardId}")
    public ResponseEntity<Void> blockCardRequest(@PathVariable(name = "cardId") Long cardId) {
        
        userService.requestCardBlock(cardId);
        
        return ResponseEntity.status(200).build();
    }
    
    


        /**
     * Получает список карт текущего пользователя с пагинацией
     *
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return ResponseEntity с Page объектов CardDto и статусом 200 (OK)
     */

    @GetMapping("/cards")
    public ResponseEntity<Page<CardDto>> getUserCards (
            @Parameter(description = "Номер страницы") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") 
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.status(200).body(userService.getUserCards(page,size));
    }

    

        /**
     * Получает баланс на указанной карте пользователя
     *
     * @param cardId ID карты для проверки баланса
     * @return ResponseEntity с суммой баланса и статусом 200 (OK)
     * @throws CardNotFoundException если карта не найдена
     * @throws UserNotOwnsThisCardException если карта не принадлежит текущему пользователю
     */

    @GetMapping("/check-ballance/{cardId}")
    public ResponseEntity<BigDecimal> getBallance(@PathVariable(name = "cardId") Long cardId) {
        return ResponseEntity.status(200).body(userService.getUserBallance(cardId));
    }

       /**
     * Получает полный расшифрованный номер карты пользователя
     *
     * @param cardId ID карты для получения номера
     * @return ResponseEntity с расшифрованным номером карты и статусом 200 (OK)
     * @throws CardNotFoundException если карта не найдена
     * @throws UserNotOwnsThisCardException если карта не принадлежит текущему пользователю
     */

    @GetMapping("/get-decrypt-card-number/{cardId}")
    public ResponseEntity<String> getDecryptCardNumber(@PathVariable(name = "cardId") Long cardId) {
     
        return ResponseEntity.status(200).body(userService.getDecryptCardNumber(cardId));
    }

    
    
}
