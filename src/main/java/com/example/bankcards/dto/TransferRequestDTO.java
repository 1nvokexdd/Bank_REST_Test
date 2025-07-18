package com.example.bankcards.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.AssertTrue;
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
public class TransferRequestDTO {

    @NotNull
    @NotNull(message = "ID исходной карты обязательно для заполнения")
    @Positive(message = "ID исходной карты должен быть положительным числом")
    private Long fromCardId;

    @NotNull(message = "ID целевой карты обязательно для заполнения")
    @Positive(message = "ID целевой карты должен быть положительным числом")
    private Long toCardId;


    @NotNull(message = "Сумма перевода обязательна для заполнения")
    @DecimalMin(value = "0.01", message = "Сумма перевода должна быть не менее 0.01")
    @Digits(integer = 10, fraction = 2, message = "Сумма должна содержать максимум 10 целых и 2 дробных цифры")
    private BigDecimal sum;

    
}
