package com.example.bankcards.exception;

public class UserException {
    
public  static class UserNotFoundException extends RuntimeException {
    public UserNotFoundException () {
        super("Пользователь не найден");
    }
}

public  static class UserNotOwnsThisCardException extends RuntimeException {
    public UserNotOwnsThisCardException (Long userId , Long cardId) {
        super("Пользователь с id:  " + userId + " не владеет картой: " + cardId);
    }
}


public  static class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException () {
        super("Пользователь с этими данными уже существует");
    }
}
}
