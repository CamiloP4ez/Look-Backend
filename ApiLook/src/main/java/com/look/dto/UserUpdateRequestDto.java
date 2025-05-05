package com.look.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
//import java.util.Set;

@Data
@NoArgsConstructor
public class UserUpdateRequestDto {

    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username; 

    @Email(message = "Email should be valid")
    private String email; 

    private String profilePictureUri;

   
}