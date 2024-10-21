package com.rupesh.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorMessage(
        String code,
        String message,
        HttpStatus status,
        String uri,
        LocalDateTime timestamp,
        Map<String, String> errors
) {

}