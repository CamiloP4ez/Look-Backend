package com.look.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserStatusUpdateDto {
    @NotNull(message = "Enabled status cannot be null")
    private Boolean enabled;
}