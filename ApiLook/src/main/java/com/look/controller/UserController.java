package com.look.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import com.look.dto.PostResponseDto;
import com.look.dto.UserResponseDto;
import com.look.dto.UserRoleUpdateRequestDto;
import com.look.dto.UserStatusUpdateDto;
import com.look.dto.UserUpdateRequestDto;
import com.look.jwt.JwtTokenProvider;
import com.look.service.PostService;
import com.look.service.UserService;
import com.look.jwt.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.look.dto.AuthResponseDto; 
import java.util.stream.Collectors; 

import com.look.dto.UserCreateRequestDto;    
import com.look.dto.AdminUserUpdateRequestDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Operations related to user profiles and administration")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Operation(summary = "Get current user's profile", description = "Requires authentication.")
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getMyProfile() {
        UserResponseDto userProfile = userService.getMyProfile();
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("Profile fetched successfully",
                HttpStatus.OK.value(), userProfile);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Create a new user", description = "Requires ADMIN or SUPERADMIN role. Allows setting username, email, password, and roles.")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> createUserByAdmin(
            @Valid @RequestBody UserCreateRequestDto userCreateRequestDto) {
        UserResponseDto newUser = userService.createUserByAdmin(userCreateRequestDto);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("User created successfully by admin",
                HttpStatus.CREATED.value(), newUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Update any user's profile by ID (Admin)", description = "Requires ADMIN or SUPERADMIN role. Allows updating username, email, password, roles, and enabled status.")
    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUserByAdmin(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable String userId,
            @Valid @RequestBody AdminUserUpdateRequestDto adminUserUpdateRequestDto) {
        UserResponseDto updatedUser = userService.updateUserByAdmin(userId, adminUserUpdateRequestDto);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("User profile updated successfully by admin",
                HttpStatus.OK.value(), updatedUser);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Update current user's profile", description = "Requires authentication.")
    @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<?>> updateMyProfile( 
            @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto) {

        Authentication originalAuth = SecurityContextHolder.getContext().getAuthentication();
        String originalUsername = originalAuth.getName();

        UserResponseDto updatedProfile = userService.updateMyProfile(userUpdateRequestDto); 

       
        boolean usernameChanged = userUpdateRequestDto.getUsername() != null &&
                                  !updatedProfile.getUsername().equals(originalUsername);

        if (usernameChanged) {
           
            Authentication updatedAuth = SecurityContextHolder.getContext().getAuthentication();
            String newToken = tokenProvider.generateToken(updatedAuth); 

            
            AuthResponseDto authResponse = new AuthResponseDto(
                newToken,
                updatedProfile.getId(),
                updatedProfile.getUsername(),
                updatedProfile.getEmail(),
                updatedAuth.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .collect(Collectors.toSet())
            );

            ApiResponseDto<AuthResponseDto> response = new ApiResponseDto<>(
                "Profile updated successfully. New token issued.",
                HttpStatus.OK.value(),
                authResponse
            );
            return ResponseEntity.ok(response);
        } else {
            ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>(
                "Profile updated successfully",
                HttpStatus.OK.value(),
                updatedProfile
            );
            return ResponseEntity.ok(response);
        }
    }

    @Operation(summary = "Get all users", description = "Requires ADMIN or SUPERADMIN role.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)	
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        ApiResponseDto<List<UserResponseDto>> response = new ApiResponseDto<>("Users fetched successfully",
                HttpStatus.OK.value(), users);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user profile by ID", description = "")
    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true) @PathVariable String userId) {
        UserResponseDto userProfile = userService.getUserProfile(userId);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("User profile fetched successfully",
                HttpStatus.OK.value(), userProfile);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all posts by a specific user ID", description = "Public endpoint.")
    @ApiResponse(responseCode = "200", description = "Posts fetched successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping(value = "/{userId}/posts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getPostsByUserId(
            @Parameter(description = "ID of the user whose posts are to be retrieved", required = true, in = ParameterIn.PATH) @PathVariable String userId) {
        List<PostResponseDto> posts = postService.getPostsByUserId(userId);
        ApiResponseDto<List<PostResponseDto>> response = new ApiResponseDto<>("Posts by user fetched successfully",
                HttpStatus.OK.value(), posts);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user roles", description = "Requires SUPERADMIN role.")
    @PutMapping(value = "/{userId}/roles", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUserRoles(
            @Parameter(description = "ID of the user whose roles are to be updated", required = true) @PathVariable String userId,
            @Valid @RequestBody UserRoleUpdateRequestDto roleUpdateRequestDto) {
        UserResponseDto updatedUser = userService.updateUserRoles(userId, roleUpdateRequestDto);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("User roles updated successfully",
                HttpStatus.OK.value(), updatedUser);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user enabled status", description = "Requires ADMIN or SUPERADMIN role.")
    @PatchMapping(value = "/{userId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUserStatus(
            @Parameter(description = "ID of the user whose status is to be updated", required = true) @PathVariable String userId,
            @Valid @RequestBody UserStatusUpdateDto statusUpdateDto) {
        UserResponseDto updatedUser = userService.setUserEnabledStatus(userId, statusUpdateDto);
        ApiResponseDto<UserResponseDto> response = new ApiResponseDto<>("User status updated successfully",
                HttpStatus.OK.value(), updatedUser);
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
    @Operation(summary = "Follow a user", description = "Requires authentication. Current user follows the user specified by userIdToFollow.")
    @PostMapping("/{userIdToFollow}/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<Void>> followUser(
            @Parameter(description = "ID of the user to follow", required = true) @PathVariable String userIdToFollow) {
        userService.followUser(userIdToFollow);
        ApiResponseDto<Void> response = new ApiResponseDto<>("User followed successfully", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Unfollow a user", description = "Requires authentication. Current user unfollows the user specified by userIdToUnfollow.")
    @DeleteMapping("/{userIdToUnfollow}/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<Void>> unfollowUser(
            @Parameter(description = "ID of the user to unfollow", required = true) @PathVariable String userIdToUnfollow) {
        userService.unfollowUser(userIdToUnfollow);
        ApiResponseDto<Void> response = new ApiResponseDto<>("User unfollowed successfully", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a user's followers", description = "Requires authentication. Lists users who follow the user specified by userId.")
    @GetMapping("/{userId}/followers")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getFollowers(
            @Parameter(description = "ID of the user whose followers to list", required = true) @PathVariable String userId) {
        List<UserResponseDto> followers = userService.getFollowers(userId);
        ApiResponseDto<List<UserResponseDto>> response = new ApiResponseDto<>("Followers fetched successfully", HttpStatus.OK.value(), followers);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get users a user is following", description = "Requires authentication. Lists users whom the user specified by userId is following.")
    @GetMapping("/{userId}/following")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getFollowing(
            @Parameter(description = "ID of the user whose following list to retrieve", required = true) @PathVariable String userId) {
        List<UserResponseDto> following = userService.getFollowing(userId);
        ApiResponseDto<List<UserResponseDto>> response = new ApiResponseDto<>("Following list fetched successfully", HttpStatus.OK.value(), following);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get a personalized feed for the current user", description = "Requires authentication. Shows posts from users the current user is following.")
    @GetMapping("/me/feed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getCurrentUserFeed() {
        List<PostResponseDto> feedPosts = postService.getFeedForCurrentUser();
        ApiResponseDto<List<PostResponseDto>> response = new ApiResponseDto<>("Feed fetched successfully", HttpStatus.OK.value(), feedPosts);
        return ResponseEntity.ok(response);
    }
}