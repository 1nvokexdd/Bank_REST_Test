package com.example.bankcards.config.annotaions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
    String message() default "Invalid Phone Number";
    Class<?>[] groups () default{};
    Class<? extends Payload>[] payload() default {};
    String region() default "RU";
}
