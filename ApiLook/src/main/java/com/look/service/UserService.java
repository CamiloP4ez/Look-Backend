
package com.look.service;

import com.look.dto.UserResponseDto;
import com.look.dto.UserRoleUpdateRequestDto;
import com.look.dto.UserStatusUpdateDto;
import com.look.dto.UserUpdateRequestDto;
import com.look.dto.AdminUserUpdateRequestDto;
import com.look.dto.UserCreateRequestDto; 

import java.util.List;

public interface UserService {

    UserResponseDto getUserProfile(String userId);

    UserResponseDto getMyProfile();

    UserResponseDto updateMyProfile(UserUpdateRequestDto userUpdateRequestDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto updateUserRoles(String userId, UserRoleUpdateRequestDto roleUpdateRequestDto);

    void deleteUser(String userId);

    UserResponseDto setUserEnabledStatus(String userId, UserStatusUpdateDto statusUpdateDto);
    
    void followUser(String userIdToFollow);
    void unfollowUser(String userIdToUnfollow);
    List<UserResponseDto> getFollowers(String userId);
    List<UserResponseDto> getFollowing(String userId);
    
    UserResponseDto createUserByAdmin(UserCreateRequestDto userCreateRequestDto);
    UserResponseDto updateUserByAdmin(String userId, AdminUserUpdateRequestDto adminUserUpdateRequestDto);
}