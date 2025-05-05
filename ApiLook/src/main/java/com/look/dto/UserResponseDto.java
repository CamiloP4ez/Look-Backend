package com.look.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserResponseDto {
    private String id;
    private String username;
    private String email;
    private String profilePictureUri;
    private Date createdAt;
    private Set<String> roles;
    private boolean enabled;
}