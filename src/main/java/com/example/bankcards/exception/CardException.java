package com.example.bankcards.exception;

public class CardException {
    
public static class CardNotFoundException extends RuntimeException {
     public CardNotFoundException(Long cardId) {
            super(String.format("Карта с ID %d не найдена", cardId));
    }
}


public  static class CardTransferMoneyException extends RuntimeException {
    public CardTransferMoneyException (Long sourceCardId , Long targetCardId) {
         super(String.format("Ошибка перевода между картами %d и %d", sourceCardId, targetCardId));
    }

    public CardTransferMoneyException(Long sourceCardId, Long targetCardId, String reason) {
            super(String.format("Ошибка перевода между картами %d и %d: %s", sourceCardId, targetCardId, reason));
    }
}


public  static class CardBlockException extends RuntimeException {
    public CardBlockException (Long cardId ) {
        super("Ошибка блокировки карты " + cardId);
    }
}


public  static class CardCreateException extends RuntimeException {
    public CardCreateException (String cardNumber ) {
        super("Ошибка создания карты  " + cardNumber);
    }
}



public  static class CardRequestBlockException extends RuntimeException {
    public CardRequestBlockException (Long cardId , String reason) {
        super(String.format("Ошибка запроса блокировки карты:  %s ", reason));
    }
}


}
