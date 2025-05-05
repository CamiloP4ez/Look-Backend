package com.look.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class ChatResponseDto {
    private String id;
    private String user1Id;
    private String user1Username; 
    private String user2Id;
    private String user2Username; 
    private List<MessageDto> messages; 
    private MessageDto lastMessage; 
}