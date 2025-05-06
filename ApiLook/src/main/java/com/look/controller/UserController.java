package com.look.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.look.dto.ApiResponseDto;
import com.look.dto.UserResponseDto;
import com.look.dto.UserRoleUpdateRequestDto;
import com.look.dto.UserStatusUpdateDto;
import com.look.dto.UserUpdateRequestDto;
import com.look.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Operations related to user profiles and administration")
@SecurityRequirement(name = "bearerAuth") // La mayoría requiere auth
public class UserController {

    @Autowired
    UserService  userService;

    @Operation(summary = "Get current user's profile", description = "Requires authentication.")
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto  <UserResponseDto >> getMyProfile() {
        UserResponseDto userProfile = userService.getMyProfile();
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("Profile fetched successfully", HttpStatus.OK.value(), userProfile);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update current user's profile", description = "Requires authentication.")
    @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateMyProfile(
            @Valid @RequestBody UserUpdateRequestDto  userUpdateRequestDto) {
        UserResponseDto updatedProfile = userService.updateMyProfile(userUpdateRequestDto);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("Profile updated successfully", HttpStatus.OK.value(), updatedProfile);
        return ResponseEntity.ok(response);
    }


    // --- Admin/SuperAdmin Endpoints ---

    @Operation(summary = "Get all users", description = "Requires ADMIN or SUPERADMIN role.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        ApiResponseDto<List<UserResponseDto>> response = new ApiResponseDto<>("Users fetched successfully", HttpStatus.OK.value(), users);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user profile by ID", description = "Requires ADMIN or SUPERADMIN role.")
    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable String userId) {
        UserResponseDto userProfile = userService.getUserProfile(userId);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("User profile fetched successfully", HttpStatus.OK.value(), userProfile);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user roles", description = "Requires SUPERADMIN role.")
    @PutMapping(value = "/{userId}/roles", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUserRoles(
            @Parameter(description = "ID of the user whose roles are to be updated", required = true) @PathVariable String userId,
            @Valid @RequestBody UserRoleUpdateRequestDto  roleUpdateRequestDto) {
        UserResponseDto updatedUser = userService.updateUserRoles(userId, roleUpdateRequestDto);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("User roles updated successfully", HttpStatus.OK.value(), updatedUser);
        return ResponseEntity.ok(response);
    }

     @Operation(summary = "Update user enabled status", description = "Requires ADMIN or SUPERADMIN role.")
     @PatchMapping(value = "/{userId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) // PATCH para actualización parcial
     @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
     public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUserStatus(
             @Parameter(description = "ID of the user whose status is to be updated", required = true) @PathVariable String userId,
             @Valid @RequestBody UserStatusUpdateDto  statusUpdateDto) {
         UserResponseDto updatedUser = userService.setUserEnabledStatus(userId, statusUpdateDto);
         ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("User status updated successfully", HttpStatus.OK.value(), updatedUser);
         return ResponseEntity.ok(response);
     }


    @Operation(summary = "Delete a user", description = "Requires SUPERADMIN role.")
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true) @PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}