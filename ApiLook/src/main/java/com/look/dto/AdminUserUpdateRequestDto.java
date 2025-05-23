package com.look.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserUpdateRequestDto {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "New username for the user (optional)", example = "updatedUser")
    private String username;

    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    @Schema(description = "New email for the user (optional)", example = "updated@example.com")
    private String email;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Schema(description = "New password for the user (optional, will be hashed)", example = "newStrongPassword123")
    private String password;

    @Schema(description = "URL of the user's profile picture (optional)", example = "http://example.com/newpic.jpg")
    private String profilePictureUri;

    @Schema(description = "Set of roles to assign to the user (optional, replaces existing roles if provided)",
            example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private Set<String> roles;

    @Schema(description = "Whether the user account should be enabled (optional)", example = "false")
    private Boolean enabled;
}