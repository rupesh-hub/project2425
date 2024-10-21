package com.rupesh.shared.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

      /* Password Pattern Explanation:
            ^(?=.*[A-Z]): Ensures that the password contains at least one uppercase letter.
            (?=.*[!@#$%^&*()_+=<>?]): Ensures that the password contains at least one special character (you can modify this set as needed).
            (?=.*[0-9]): Ensures that the password contains at least one digit.
            .{8,}$: Ensures that the password has a minimum length of 8 characters.
      */
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+=<>?])(?=.*[0-9]).{8,}$";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        return password.matches(PASSWORD_PATTERN);
    }
}

