package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.example.bankcards.entity.CARD_STATUS;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CardDto {
    private Long id;
    private String bin;
    private  String lastFour;
    private String cvv;
    private LocalDate  expirationDate;
    private CARD_STATUS status;
    private BigDecimal ballance;
}
