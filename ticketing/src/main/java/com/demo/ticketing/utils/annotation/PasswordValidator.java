package com.demo.ticketing.utils.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(
        validatedBy = {PasswordValidatorDefinition.class}
)
@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordValidator {
    String message() default "le mot de passe doit contenir un caractère majuscule, miniscule, spéciaux, numérique et au minimum huit caractère";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
