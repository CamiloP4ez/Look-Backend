package com.look.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import jakarta.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class UserRoleUpdateRequestDto {
    @NotEmpty(message = "Roles cannot be empty")
    private Set<String> roles; 
}