package com.example.bankcards.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.CreatedDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CARD_TABLE")
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String bin;

    @JsonIgnore
    @Transient
    private String number;

    private  String lastFour;

    private String encryptedCardNumber;

    private String cvv;

    @JsonIgnore
     @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @CreatedDate
    private LocalDate createDate;

    private LocalDate  expirationDate;

    @Enumerated(EnumType.STRING)
    private CARD_STATUS status;
    
    private BigDecimal ballance;

      public String getMaskedNumber() {
        return "**** **** **** " + lastFour;
    }
}
