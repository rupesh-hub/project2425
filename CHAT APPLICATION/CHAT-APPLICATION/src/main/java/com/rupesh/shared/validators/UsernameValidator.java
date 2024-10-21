package com.rupesh.shared.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    private static final List<String> DISALLOWED_USERNAMES = Arrays.asList("admin", "password");

    @Override
    public void initialize(ValidUsername constraintAnnotation) {
        // No initialization needed in this case, but we can handle any annotation parameters here
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return false;
        }

        if (username.length() < 8) {
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9]+$")) {
            return false; // Allow only alphanumeric characters
        }

        if (DISALLOWED_USERNAMES.contains(username.toLowerCase())) {
            return false;
        }

        return true;
    }
}
