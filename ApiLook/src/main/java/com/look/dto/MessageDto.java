package com.look.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class MessageDto {
    private String senderId;
    private String senderUsername; 
    private String message;
    private Date timestamp;
}