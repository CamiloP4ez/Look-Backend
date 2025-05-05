package com.look.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatCreateRequestDto {
    @NotBlank(message = "Other user ID cannot be blank")
    private String otherUserId;
}