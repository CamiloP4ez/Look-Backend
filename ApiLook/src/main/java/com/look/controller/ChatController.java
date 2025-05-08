package com.look.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.look.dto.ApiResponseDto;
import com.look.dto.ChatCreateRequestDto;
import com.look.dto.ChatResponseDto;
import com.look.dto.MessageDto;
import com.look.dto.MessageRequestDto;
import com.look.service.ChatService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@Tag(name = "Chats", description = "Operations related to chats and messages")
@SecurityRequirement(name = "bearerAuth") // Todas las operaciones de chat requieren autenticación
@PreAuthorize("isAuthenticated()") // Aplicar a nivel de clase ya que todo requiere auth
public class ChatController {

    @Autowired
    ChatService  chatService;

    @Operation(summary = "Get or Create a chat with another user", description = "Finds an existing chat or creates a new one.")
    @PostMapping(value = "/findOrCreate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto <ChatResponseDto >> getOrCreateChat(
            @Valid @RequestBody ChatCreateRequestDto  createRequestDto) {
        ChatResponseDto chat = chatService.getOrCreateChat(createRequestDto);
        // Determinar si fue creado o encontrado para el mensaje/código (opcional)
        // Por simplicidad, siempre devolvemos OK aquí
        ApiResponseDto<ChatResponseDto> response = new ApiResponseDto<>("Chat retrieved or created successfully", HttpStatus.OK.value(), chat);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all chats for the current user", description = "Retrieves a list of chats the user participates in.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<List<ChatResponseDto>>> getMyChats() {
        List<ChatResponseDto> chats = chatService.getMyChats();
        ApiResponseDto<List<ChatResponseDto>> response = new ApiResponseDto<>("Chats fetched successfully", HttpStatus.OK.value(), chats);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Send a message in a chat", description = "Adds a message to the specified chat.")
    @PostMapping(value = "/{chatId}/messages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<MessageDto >> sendMessage(
            @Parameter(description = "ID of the chat to send the message to", required = true) @PathVariable String chatId,
            @Valid @RequestBody MessageRequestDto  messageRequestDto) {
        MessageDto sentMessage = chatService.sendMessage(chatId, messageRequestDto);
        ApiResponseDto<MessageDto> response = new ApiResponseDto<>("Message sent successfully", HttpStatus.CREATED.value(), sentMessage);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get messages for a specific chat", description = "Retrieves all messages within a given chat.")
    @GetMapping(value = "/{chatId}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<List<MessageDto>>> getChatMessages(
            @Parameter(description = "ID of the chat whose messages are to be retrieved", required = true) @PathVariable String chatId) {
        List<MessageDto> messages = chatService.getChatMessages(chatId);
         ApiResponseDto<List<MessageDto>> response = new ApiResponseDto<>("Messages fetched successfully", HttpStatus.OK.value(), messages);
        return ResponseEntity.ok(response);
    }
}