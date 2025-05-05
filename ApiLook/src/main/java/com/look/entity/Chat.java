package com.look.entity;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat")
public class Chat {

    @Id
    private String id; 

    @Field("user1_id") 
    private String user1Id;

    @Field("user2_id") 
    private String user2Id;

    @Field("messages") 
    @Builder.Default 
    private List<Message> messages = new ArrayList<>();
}
