package com.look.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ApiResponseDto<T> {
    private LocalDateTime timestamp;
    private String message;
    private int code;
    private T data;

    public ApiResponseDto(String message, int code, T data) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.code = code;
        this.data = data;
    }

     public ApiResponseDto(String message, int code) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.code = code;
        this.data = null; 
    }
}