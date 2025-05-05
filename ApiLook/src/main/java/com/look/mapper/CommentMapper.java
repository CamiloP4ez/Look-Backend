package com.look.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.look.dto.CommentRequestDto;
import com.look.dto.CommentResponseDto;
import com.look.entity.Comment;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postId", ignore = true) 
    @Mapping(target = "userId", ignore = true) 
    @Mapping(target = "createdAt", ignore = true) 
    Comment commentRequestDtoToComment(CommentRequestDto dto);

    @Mapping(target = "authorUsername", ignore = true) 
    CommentResponseDto commentToCommentResponseDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateCommentFromDto(CommentRequestDto dto, @MappingTarget Comment comment);
}