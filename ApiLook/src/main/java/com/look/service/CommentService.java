// src/main/java/com/look/service/CommentService.java
package com.look.service;

import com.look.dto.CommentRequestDto;
import com.look.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {

    CommentResponseDto createComment(String postId, CommentRequestDto commentRequestDto);

    List<CommentResponseDto> getCommentsByPostId(String postId);

    void deleteComment(String commentId);

    CommentResponseDto updateComment(String commentId, CommentRequestDto commentRequestDto);
}