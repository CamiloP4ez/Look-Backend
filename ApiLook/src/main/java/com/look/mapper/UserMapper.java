package com.look.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.look.dto.AuthRegisterRequestDto;
import com.look.dto.UserResponseDto;
import com.look.dto.UserUpdateRequestDto;
import com.look.entity.Role;
import com.look.entity.User;
import org.mapstruct.Named;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "profilePictureUri", ignore = true)
    User registerDtoToUser(AuthRegisterRequestDto registerDto);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRoleNames")
    UserResponseDto userToUserResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateUserFromDto(UserUpdateRequestDto dto, @MappingTarget User user);

    @Named("rolesToRoleNames")
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    @AfterMapping
    default void setUserCounts(User user, @MappingTarget UserResponseDto dto) {
        if (user != null) {
            Set<User> followers = user.getFollowers();
            Set<User> following = user.getFollowing();

            dto.setFollowersCount(followers != null ? followers.size() : 0L);
            dto.setFollowingCount(following != null ? following.size() : 0L);
        }
    }

}