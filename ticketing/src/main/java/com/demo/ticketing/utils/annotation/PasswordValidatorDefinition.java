package com.demo.ticketing.utils.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidatorDefinition implements ConstraintValidator<PasswordValidator,String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return (value.length() >= 8) && (Pattern.matches("[a-zA-Z0-9{$&/_~.?;!}]",value));
    }
}
