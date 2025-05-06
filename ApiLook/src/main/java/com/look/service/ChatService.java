// src/main/java/com/look/service/ChatService.java
package com.look.service;

import com.look.dto.ChatCreateRequestDto;
import com.look.dto.ChatResponseDto;
import com.look.dto.MessageDto;
import com.look.dto.MessageRequestDto;

import java.util.List;

public interface ChatService {

    ChatResponseDto getOrCreateChat(ChatCreateRequestDto createRequestDto);

    MessageDto sendMessage(String chatId, MessageRequestDto messageRequestDto);

    List<ChatResponseDto> getMyChats();

    List<MessageDto> getChatMessages(String chatId);
}