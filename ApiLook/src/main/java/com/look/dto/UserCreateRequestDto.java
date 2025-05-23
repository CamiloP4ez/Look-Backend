package com.look.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDto {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Desired username for the new user", example = "newuser123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    @Schema(description = "Email address for the new user", example = "newuser@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Schema(description = "Password for the new user", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotEmpty(message = "User must have at least one role")
    @Schema(description = "Set of roles to assign to the user (e.g., [\"ROLE_USER\", \"ROLE_MODERATOR\"])",
            example = "[\"ROLE_USER\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<String> roles;

    @Schema(description = "URL of the user's profile picture (optional)", example = "http://example.com/pic.jpg")
    private String profilePictureUri;

    @Schema(description = "Whether the user account should be enabled (optional, defaults to true)", example = "true")
    private Boolean enabled = true; // Default to true
}