package com.look.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message { 

    @Field("sender_id") 
    private String senderId;

    @Field("message")
    private String message;

    @Field("timestamp")
    private Date timestamp;
}