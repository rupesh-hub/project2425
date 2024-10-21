package com.rupesh.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalResponse<T> {

    private String message;
    private String status;
    private String code;
    private String error;
    private List<String> errors;
    private T data;
    private Paging page;

    public static <T> GlobalResponse<T> success(T data) {
        return GlobalResponse.<T>builder()
                .message("success")
                .status("success")
                .code("200")
                .data(data)
                .build();
    }

    public static <T> GlobalResponse<T> success(T data, Paging page) {
        return GlobalResponse.<T>builder()
                .message("success")
                .status("success")
                .code("200")
                .data(data)
                .page(page)
                .build();
    }

    public static <T> GlobalResponse<T> success(String message, T data) {
        return GlobalResponse
                .<T>builder()
                .message(message)
                .status("success")
                .code("200")
                .data(data)
                .build();
    }

    public static GlobalResponse<Void> success(String message) {
        return GlobalResponse
                .<Void>builder()
                .message(message)
                .status("success")
                .code("200")
                .build();
    }

    public static GlobalResponse<Void> success() {
        return GlobalResponse
                .<Void>builder()
                .status("success")
                .code("200")
                .build();
    }

    public static GlobalResponse<Void> failure(String errorMessage) {
        return GlobalResponse
                .<Void>builder()
                .status("failure")
                .code("500")
                .error(errorMessage)
                .build();
    }

    public static GlobalResponse<Void> failure(List<String> errors) {
        return GlobalResponse
                .<Void>builder()
                .status("failure")
                .code("500")
                .errors(errors)
                .build();
    }
}
