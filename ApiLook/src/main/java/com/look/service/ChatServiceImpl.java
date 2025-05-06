
package com.look.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.look.dto.ChatCreateRequestDto;
import com.look.dto.ChatResponseDto;
import com.look.dto.MessageDto;
import com.look.dto.MessageRequestDto;
import com.look.entity.Chat;
import com.look.entity.Message;
import com.look.entity.User;
import com.look.exception.BadRequestException;
import com.look.exception.ResourceNotFoundException;
import com.look.exception.UnauthorizedException;
import com.look.mapper.ChatMapper;
import com.look.mapper.MessageMapper;
import com.look.repository.ChatRepository;
import com.look.repository.UserRepository;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService { 

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChatMapper chatMapper;

    @Autowired
    MessageMapper messageMapper;

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

     private ChatResponseDto enrichChatResponse(ChatResponseDto dto, Map<String, String> userMap) {
        dto.setUser1Username(userMap.getOrDefault(dto.getUser1Id(), "Unknown User"));
        dto.setUser2Username(userMap.getOrDefault(dto.getUser2Id(), "Unknown User"));
        if (dto.getMessages() != null && !dto.getMessages().isEmpty()) {
            dto.setLastMessage(dto.getMessages().get(dto.getMessages().size() - 1));
            dto.getMessages().forEach(msg -> msg.setSenderUsername(userMap.getOrDefault(msg.getSenderId(), "Unknown User")));
        }
        return dto;
    }

     private MessageDto enrichMessageDto(MessageDto dto, Map<String, String> userMap) {
         dto.setSenderUsername(userMap.getOrDefault(dto.getSenderId(), "Unknown User"));
         return dto;
     }

    private Map<String, String> getUsernames(Set<String> userIds) {
         Map<String, String> userMap = new HashMap<>();
         userRepository.findAllById(userIds).forEach(user -> userMap.put(user.getId(), user.getUsername()));
         return userMap;
     }

    // --- Public Service Methods  ---

    @Override 
    @Transactional
    public ChatResponseDto getOrCreateChat(ChatCreateRequestDto createRequestDto) {
        User currentUser = getCurrentAuthenticatedUser();
        String currentUserId = currentUser.getId();
        String otherUserId = createRequestDto.getOtherUserId();

        if (currentUserId.equals(otherUserId)) {
            throw new BadRequestException("Cannot create a chat with yourself");
        }

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Other user not found with id: " + otherUserId));

        Optional<Chat> existingChatOpt = chatRepository.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(
                currentUserId, otherUserId, currentUserId, otherUserId);

        Chat chat = existingChatOpt.orElseGet(() -> {
            Chat newChat = Chat.builder()
                    .user1Id(currentUserId)
                    .user2Id(otherUserId)
                    .messages(new ArrayList<>())
                    .build();
            return chatRepository.save(newChat);
        });

        Map<String, String> userMap = getUsernames(Set.of(chat.getUser1Id(), chat.getUser2Id()));
        ChatResponseDto responseDto = chatMapper.chatToChatResponseDto(chat);
        return enrichChatResponse(responseDto, userMap);
    }

    @Override 
    @Transactional
    public MessageDto sendMessage(String chatId, MessageRequestDto messageRequestDto) {
        User currentUser = getCurrentAuthenticatedUser();
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));

        if (!chat.getUser1Id().equals(currentUser.getId()) && !chat.getUser2Id().equals(currentUser.getId())) {
            throw new UnauthorizedException("User not authorized to send messages in this chat");
        }

        Message message = messageMapper.messageRequestDtoToMessage(messageRequestDto);
        message.setSenderId(currentUser.getId());
        message.setTimestamp(new Date());

        chat.getMessages().add(message);
        Chat updatedChat = chatRepository.save(chat);

        MessageDto responseMessageDto = messageMapper.messageToMessageDto(message);
        Map<String, String> userMap = getUsernames(Set.of(currentUser.getId()));
        return enrichMessageDto(responseMessageDto, userMap);
    }

    @Override 
    public List<ChatResponseDto> getMyChats() {
        User currentUser = getCurrentAuthenticatedUser();
        List<Chat> chats = chatRepository.findByUser1IdOrUser2Id(currentUser.getId(), currentUser.getId());

        Set<String> userIds = new HashSet<>();
        chats.forEach(chat -> {
            userIds.add(chat.getUser1Id());
            userIds.add(chat.getUser2Id());
        });

        Map<String, String> userMap = getUsernames(userIds);

        return chats.stream()
                .map(chatMapper::chatToChatResponseDto)
                .map(dto -> enrichChatResponse(dto, userMap))
                .sorted(Comparator.comparing(
                    (ChatResponseDto dto) -> Optional.ofNullable(dto.getLastMessage())
                                                    .map(MessageDto::getTimestamp)
                                                    .orElse(new Date(0))
                ).reversed())
                .collect(Collectors.toList());
    }

    @Override 
     public List<MessageDto> getChatMessages(String chatId) {
        User currentUser = getCurrentAuthenticatedUser();
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));

        if (!chat.getUser1Id().equals(currentUser.getId()) && !chat.getUser2Id().equals(currentUser.getId())) {
            throw new UnauthorizedException("User not authorized to view messages in this chat");
        }

        Set<String> userIds = chat.getMessages().stream().map(Message::getSenderId).collect(Collectors.toSet());
         userIds.add(chat.getUser1Id());
         userIds.add(chat.getUser2Id());
         Map<String, String> userMap = getUsernames(userIds);

        return chat.getMessages().stream()
                .map(messageMapper::messageToMessageDto)
                .map(dto -> enrichMessageDto(dto, userMap))
                .sorted(Comparator.comparing(MessageDto::getTimestamp))
                .collect(Collectors.toList());
    }
}