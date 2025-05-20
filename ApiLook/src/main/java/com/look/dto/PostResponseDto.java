package com.look.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class PostResponseDto {
    private String id;
    private String userId;
    private String username;
    private String title;
    private String content;
    private String imageUri;
    private Date createdAt;
    private long likeCount; 
    
}