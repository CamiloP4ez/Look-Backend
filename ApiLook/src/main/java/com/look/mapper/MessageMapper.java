package com.look.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.look.dto.MessageDto;
import com.look.dto.MessageRequestDto;
import com.look.entity.Message;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MessageMapper {

    @Mapping(target = "senderId", ignore = true) // Se establece en el servicio
    @Mapping(target = "timestamp", ignore = true) // Se establece en el servicio
    Message messageRequestDtoToMessage(MessageRequestDto dto);

    @Mapping(target = "senderUsername", ignore = true) // Se enriquece en el servicio
    MessageDto messageToMessageDto(Message message);

    List<MessageDto> messagesToMessageDtos(List<Message> messages);
}