
package com.look.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.look.dto.UserResponseDto;
import com.look.dto.UserRoleUpdateRequestDto;
import com.look.dto.UserStatusUpdateDto;
import com.look.dto.UserUpdateRequestDto;
import com.look.entity.Role;
import com.look.entity.User;
import com.look.exception.BadRequestException;
import com.look.exception.ResourceNotFoundException;
import com.look.exception.UnauthorizedException;
import com.look.mapper.UserMapper;
import com.look.repository.RoleRepository;
import com.look.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service 
public class UserServiceImpl implements UserService { 

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder; 
    
    @Autowired 
    RoleRepository roleRepository;

     // --- Helper Methods ---
    private User getCurrentAuthenticatedUser() {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
             throw new UnauthorizedException("User not authenticated");
         }
         String username = authentication.getName();
         return userRepository.findByUsername(username)
                 .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found in database: " + username));
    }

    // --- Public Service Methods ---

    @Override
    public UserResponseDto getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return userMapper.userToUserResponseDto(user);
    }

    @Override
    public UserResponseDto getMyProfile() {
        User currentUser = getCurrentAuthenticatedUser();
        return userMapper.userToUserResponseDto(currentUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateMyProfile(UserUpdateRequestDto userUpdateRequestDto) {
        User currentUser = getCurrentAuthenticatedUser();

        if (userUpdateRequestDto.getUsername() != null && !userUpdateRequestDto.getUsername().equals(currentUser.getUsername())) {
            if (userRepository.existsByUsername(userUpdateRequestDto.getUsername())) {
                throw new BadRequestException("Username is already taken!");
            }
        }
        if (userUpdateRequestDto.getEmail() != null && !userUpdateRequestDto.getEmail().equals(currentUser.getEmail())) {
             if (userRepository.existsByEmail(userUpdateRequestDto.getEmail())) {
                throw new BadRequestException("Email is already in use!");
            }
        }

        userMapper.updateUserFromDto(userUpdateRequestDto, currentUser);
        User updatedUser = userRepository.save(currentUser);
        return userMapper.userToUserResponseDto(updatedUser);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::userToUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponseDto updateUserRoles(String userId, UserRoleUpdateRequestDto roleUpdateRequestDto) {
        User user = userRepository.findById(userId)
                 .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

     
        Set<String> requestedRoleNames = roleUpdateRequestDto.getRoles();
        Set<Role> newRoles = roleRepository.findByNameIn(requestedRoleNames);

        if (newRoles.size() != requestedRoleNames.size()) {
             Set<String> foundNames = newRoles.stream().map(Role::getName).collect(Collectors.toSet());
             requestedRoleNames.removeAll(foundNames); // Nombres que no se encontraron
             throw new BadRequestException("Invalid role(s) specified: " + requestedRoleNames);
        }

        user.setRoles(newRoles); 
        User updatedUser = userRepository.save(user);
        return userMapper.userToUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        User currentUser = getCurrentAuthenticatedUser();
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (currentUser.getId().equals(userToDelete.getId())) {
            throw new BadRequestException("Cannot delete your own account using this endpoint.");
        }


        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
     public UserResponseDto setUserEnabledStatus(String userId, UserStatusUpdateDto statusUpdateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

         User currentUser = getCurrentAuthenticatedUser();
         if (currentUser.getId().equals(user.getId())) {
             throw new BadRequestException("Cannot disable your own account.");
         }

        user.setEnabled(statusUpdateDto.getEnabled());
        if (!statusUpdateDto.getEnabled()) {
            user.setAccountNonLocked(false);
        } else {
             user.setAccountNonLocked(true);
        }

        User updatedUser = userRepository.save(user);
        return userMapper.userToUserResponseDto(updatedUser);
    }
}