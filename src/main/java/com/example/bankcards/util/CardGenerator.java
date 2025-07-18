package com.example.bankcards.util;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;
import com.example.bankcards.entity.CARD_STATUS;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardException.CardCreateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Component
@Slf4j
@RequiredArgsConstructor
public class CardGenerator {

   private final StringEncryptor encryptor ;
    private static final SecureRandom random = new SecureRandom();
    
    
    
    private static final List<String> VISA_PREFIXES = Arrays.asList(
        "4539", "4556", "4916", "4532", "4929", "4024", "4485", "4716"
    );


     /**
     * Генерирует BIN (Bank Identification Number) 
     * 
     * @return 6-значный BIN код 
     */
    public static String generateVisaBin() {
        String prefix = VISA_PREFIXES.get(random.nextInt(VISA_PREFIXES.size()));
        StringBuilder bin = new StringBuilder(prefix);

        while (bin.length() < 6) {
            bin.append(random.nextInt(10));
        }
        
        return bin.toString();
    }
    
     /**
     * Генерирует дату истечения срока действия карты (текущая дата + 3 года)
     * 
     * @return дата истечения срока действия
     */
    public static LocalDate generateExpirationDate() {
        return LocalDate.now().plusYears(3);
    }


     /**
     * Генерирует валидный номер карты Visa (16 цифр) с проверкой по алгоритму Луна
     * 
     * @return 16-значный номер карты Visa
     */
    public static String generateVisaCardNumber() {
        String bin = generateVisaBin();
        StringBuilder cardNumber = new StringBuilder(bin);
        
        while (cardNumber.length() < 15) {
            cardNumber.append(random.nextInt(10));
        }
        
        int checkDigit = calculateLuhnCheckDigit(cardNumber.toString());
        cardNumber.append(checkDigit);
        
        return cardNumber.toString();
    }


    private static int calculateLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) n = (n % 10) + 1;
            }
            sum += n;
            alternate = !alternate;
        }

        return (10 - (sum % 10)) % 10;
    }

    private String generateCvv(){
        return String.format("%03d", random.nextInt(1000));
    }


    
    private String encryptCardNumber(String cardNumber) {
        try {
            if (cardNumber == null || cardNumber.length() != 16) {
                throw new IllegalArgumentException("Invalid card number length");
            }
            return encryptor.encrypt(cardNumber);
        } catch (Exception e) {
            log.error("Failed to encrypt card number: {}", cardNumber, e);
            throw new CardCreateException(cardNumber);
        }
    }

    /**
     * Дешифрует номер карты
     * 
     * @param encryptedCardNumber зашифрованный номер карты
     * @return расшифрованный номер карты
     */
    public  String decryptCardNumber(String encryptedCardNumber)  {
        return encryptor.decrypt(encryptedCardNumber);
    }


     /**
     * Генерирует новую банковскую карту для указанного владельца
     * 
     * @param owner пользователь-владелец карты
     * @return сгенерированный объект карты со следующими атрибутами:
     *         - BIN (первые 6 цифр)
     *         - Полный номер карты(не будет храниться в бд)
     *         - CVV код
     *         - Зашифрованный номер карты
     *         - Последние 4 цифры номера
     *         - Дата истечения срока (текущая дата + 3 года)
     *         - Статус ACTIVE
     *         - Дата создания (текущая дата)
     *         - Начальный баланс (0)
     * @throws CardCreateException если возникла ошибка при создании карты
     * @throws RuntimeException если возникла непредвиденная ошибка
     */
    public Card generateCard(User owner){
        String number = generateVisaCardNumber();
        String cvv = generateCvv();

        try {
                return Card.builder()
                        .bin(number.substring(0 , 6))
                        .number(number)
                        .cvv(cvv)
                        .encryptedCardNumber(encryptCardNumber(number))
                        .lastFour(number.substring(12))
                        .owner(owner)
                        .expirationDate(generateExpirationDate())
                        .status(CARD_STATUS.ACTIVE)
                        .createDate(LocalDate.now())
                        .ballance(BigDecimal.ZERO)
                            .build();
        } catch (Exception e) {
                throw new RuntimeException("Card creat error");
        }
    }

    
}