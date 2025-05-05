package com.look.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.look.dto.PostRequestDto;
import com.look.dto.PostResponseDto;
import com.look.entity.Post;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true) 
    @Mapping(target = "createdAt", ignore = true) 
    Post postRequestDtoToPost(PostRequestDto postRequestDto);

    @Mapping(target = "likeCount", ignore = true) 
    PostResponseDto postToPostResponseDto(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updatePostFromDto(PostRequestDto dto, @MappingTarget Post post);
}