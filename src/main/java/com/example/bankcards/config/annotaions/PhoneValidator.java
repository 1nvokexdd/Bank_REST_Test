package com.example.bankcards.config.annotaions;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone,String>{
    private String region;


       @Override
    public void initialize(Phone constraintAnnotation){
    this.region = constraintAnnotation.region();
    }

    @Override
    public boolean isValid(String phone,ConstraintValidatorContext context){
    if (phone==null || phone.isEmpty() || !phone.startsWith("+")) {
        return false;
    }

   
    
    PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    try {
        PhoneNumber phoneNumber  =phoneNumberUtil.parse(phone, region);

        return phoneNumberUtil.isValidNumberForRegion(phoneNumber, region);
    } catch (Exception e) {
        return false;
    }
    }
}
    
    

