package com.look.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentRequestDto {
    @NotBlank(message = "Comment content cannot be blank")
    private String content;
}