package com.demo.ticketing.utils.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidatorDefinition implements ConstraintValidator<PasswordValidator, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()_+".indexOf(ch) >= 0);
        boolean isValidLength = password.length() >= 8;

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar && isValidLength;
    }
}
