package com.look.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.look.dto.ChatResponseDto;
import com.look.entity.Chat;

import java.util.List;

@Mapper(componentModel = "spring", uses = {MessageMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChatMapper {

    @Mapping(target = "user1Username", ignore = true) // Enriquecido en servicio
    @Mapping(target = "user2Username", ignore = true) // Enriquecido en servicio
    @Mapping(target = "lastMessage", ignore = true) // Calculado en servicio
    ChatResponseDto chatToChatResponseDto(Chat chat);

    List<ChatResponseDto> chatsToChatResponseDtos(List<Chat> chats);
}
