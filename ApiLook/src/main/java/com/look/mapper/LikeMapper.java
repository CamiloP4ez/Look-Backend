package com.look.mapper;

import com.look.dto.LikeResponseDto;
import com.look.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LikeMapper {

    LikeResponseDto likeToLikeResponseDto(Like like);
}
