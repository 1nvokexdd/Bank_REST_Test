package com.example.bankcards.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBallanceRequestDTO {
    @NotNull(message = "ID карты обязательно для заполнения")
    @Positive(message = "ID карты должен быть положительным числом")
    private Long cardId;

    @NotNull(message = "Сумма обязательна для заполнения")
    @DecimalMin(value = "0.01", message = "Сумма должна быть не менее 0.01")
    @Digits(integer = 10, fraction = 2, message = "Сумма должна содержать максимум 10 целых и 2 дробных цифры")
    private BigDecimal sum;
}
