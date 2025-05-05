package com.look.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageRequestDto {
    @NotBlank(message = "Message content cannot be blank")
    private String message;
}