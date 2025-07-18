package com.example.bankcards.dto;

import com.example.bankcards.config.annotaions.Phone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
    
    @NotBlank(message = "Номер телефона обязателен")
    @Phone(region = "RU", message = "Неверный формат российского номера телефона")
    private String phoneNumber;
    
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 8, max = 255, message = "Пароль должен быть от 8 до 255 символов")
    @Pattern(
    regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$",
    message = "Пароль должен содержать минимум: 1 цифру, 1 заглавную букву, 1 строчную букву и быть длиной от 8 символов"
    )
    private String password;
}