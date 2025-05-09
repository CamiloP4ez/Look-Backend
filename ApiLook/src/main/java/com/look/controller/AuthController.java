package com.look.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.look.dto.ApiResponseDto;
import com.look.dto.AuthLoginRequestDto;
import com.look.dto.AuthRegisterRequestDto;
import com.look.dto.AuthResponseDto;
import com.look.dto.UserResponseDto;
import com.look.entity.User;
import com.look.mapper.UserMapper;
import com.look.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    UserMapper userMapper;

    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token.")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> authenticateUser(
            @Valid @RequestBody AuthLoginRequestDto loginRequest) {
        AuthResponseDto authResponse = authService.loginUser(loginRequest);
        ApiResponseDto<AuthResponseDto> response = new ApiResponseDto<>("User logged in successfully",
                HttpStatus.OK.value(), authResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register user", description = "Registers a new user with default ROLE_USER.")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or username/email already exists")
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<UserResponseDto>> registerUser(
            @Valid @RequestBody AuthRegisterRequestDto registerRequest) throws BadRequestException {
        User registeredUser = authService.registerUser(registerRequest);
        UserResponseDto userResponseDto = userMapper.userToUserResponseDto(registeredUser);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("User registered successfully",
                HttpStatus.CREATED.value(), userResponseDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}