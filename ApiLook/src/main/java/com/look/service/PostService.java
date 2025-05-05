// src/main/java/com/look/service/PostService.java
package com.look.service;

import com.look.dto.PostRequestDto;
import com.look.dto.PostResponseDto;

import java.util.List;

public interface PostService {

    PostResponseDto createPost(PostRequestDto postRequestDto);

    List<PostResponseDto> getAllPosts();

    PostResponseDto getPostById(String postId);

    PostResponseDto updatePost(String postId, PostRequestDto postRequestDto);

    void deletePost(String postId);

    void likePost(String postId);

    void unlikePost(String postId);
}