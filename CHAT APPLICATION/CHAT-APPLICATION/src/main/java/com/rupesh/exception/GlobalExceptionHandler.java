package com.rupesh.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorMessage> handleException(LockedException exp, HttpServletRequest servletRequest) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        new ErrorMessage(
                                "401",
                                "Your account is locked! Please contact admin.",
                                BAD_REQUEST,
                                servletRequest.getRequestURI(),
                                LocalDateTime.now(),
                                null
                        )
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorMessage> handleException(DisabledException exp, HttpServletRequest servletRequest) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        new ErrorMessage(
                                "401",
                                "Your account is disabled! Please contact admin.",
                                BAD_REQUEST,
                                servletRequest.getRequestURI(),
                                LocalDateTime.now(),
                                null
                        )
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessage> handleException(HttpServletRequest servletRequest) {
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        new ErrorMessage(
                                "400",
                                "Something went wrong!",
                                BAD_REQUEST,
                                servletRequest.getRequestURI(),
                                LocalDateTime.now(),
                                null
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp, HttpServletRequest servletRequest) {

        Map<String, String> errors = new HashMap<>();

        exp.getBindingResult()
                .getAllErrors()
                .forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        new ErrorMessage(
                                "400",
                                "Something went wrong!",
                                BAD_REQUEST,
                                servletRequest.getRequestURI(),
                                LocalDateTime.now(),
                                errors
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception exp, HttpServletRequest request) {
        exp.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        new ErrorMessage(
                                "500",
                                exp.getLocalizedMessage(),
                                INTERNAL_SERVER_ERROR,
                                request.getRequestURI(),
                                LocalDateTime.now(),
                                null
                        )
                );
    }

}