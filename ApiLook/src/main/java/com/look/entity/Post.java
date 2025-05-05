package com.look.entity;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "post")
public class Post {

    @Id
    private String id; 

    @Field("user_id") 
    private String userId;

    @Field("title")
    private String title;

    @Field("content")
    private String content;

    @Field("image_uri")
    private String imageUri;

    @Field("created_at")
    private Date createdAt;
}