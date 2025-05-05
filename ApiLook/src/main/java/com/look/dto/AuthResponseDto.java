package com.look.dto;



import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String tokenType = "Bearer ";
    private String userId;
    private String username;
    private String email;
    private Set<String> roles;

    public AuthResponseDto(String accessToken, String userId, String username, String email, Set<String> roles) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}