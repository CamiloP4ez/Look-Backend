package com.look.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
public class CommentResponseDto {
    private String id;
    private String postId;
    private String userId; 
    private String authorUsername; 
    private String content;
    private Date createdAt;
}