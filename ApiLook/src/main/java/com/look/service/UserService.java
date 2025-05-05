// src/main/java/com/look/service/UserService.java
package com.look.service;

import com.look.dto.UserResponseDto;
import com.look.dto.UserRoleUpdateRequestDto;
import com.look.dto.UserStatusUpdateDto;
import com.look.dto.UserUpdateRequestDto;

import java.util.List;

public interface UserService {

    UserResponseDto getUserProfile(String userId);

    UserResponseDto getMyProfile();

    UserResponseDto updateMyProfile(UserUpdateRequestDto userUpdateRequestDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUserRoles(String userId, UserRoleUpdateRequestDto roleUpdateRequestDto);

    void deleteUser(String userId);

    UserResponseDto setUserEnabledStatus(String userId, UserStatusUpdateDto statusUpdateDto);
}