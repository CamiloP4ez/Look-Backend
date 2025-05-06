
package com.look.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.look.dto.PostRequestDto;
import com.look.dto.PostResponseDto;
import com.look.entity.Like;
import com.look.entity.Post;
import com.look.entity.Role;
import com.look.entity.User;
import com.look.exception.BadRequestException;
import com.look.exception.ResourceNotFoundException;
import com.look.exception.UnauthorizedException;
import com.look.mapper.PostMapper;
import com.look.repository.LikeRepository;
import com.look.repository.PostRepository;
import com.look.repository.UserRepository;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service /
public class PostServiceImpl implements PostService { 

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    PostMapper postMapper;

    // --- Helper Methods ---
    private User getCurrentAuthenticatedUser() {
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
             throw new UnauthorizedException("User not authenticated");
         }
         String username = authentication.getName();
         return userRepository.findByUsername(username)
                 .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found in database: " + username));
    }

    private boolean isAdminOrSuperAdmin(User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN") ||
                                 role.getName().equals("ROLE_SUPERADMIN"));
    }

     private PostResponseDto enrichPostResponse(PostResponseDto dto) {
        long likeCount = likeRepository.countByPostId(dto.getId());
        dto.setLikeCount(likeCount);
        return dto;
    }

    // --- Public Service Methods ---

    @Override 
    @Transactional
    public PostResponseDto createPost(PostRequestDto postRequestDto) {
        User currentUser = getCurrentAuthenticatedUser();
        Post post = postMapper.postRequestDtoToPost(postRequestDto);
        post.setUserId(currentUser.getId());
        post.setCreatedAt(new Date());
        Post savedPost = postRepository.save(post);
        return enrichPostResponse(postMapper.postToPostResponseDto(savedPost));
    }

    @Override 
    public List<PostResponseDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(postMapper::postToPostResponseDto)
                .map(this::enrichPostResponse)
                .collect(Collectors.toList());
    }

    @Override 
     public PostResponseDto getPostById(String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        return enrichPostResponse(postMapper.postToPostResponseDto(post));
    }

    @Override 
    @Transactional
    public PostResponseDto updatePost(String postId, PostRequestDto postRequestDto) {
        User currentUser = getCurrentAuthenticatedUser();
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (!existingPost.getUserId().equals(currentUser.getId()) && !isAdminOrSuperAdmin(currentUser)) {
            throw new UnauthorizedException("User not authorized to update this post");
        }

        postMapper.updatePostFromDto(postRequestDto, existingPost);
        Post updatedPost = postRepository.save(existingPost);
        return enrichPostResponse(postMapper.postToPostResponseDto(updatedPost));
    }

    @Override 
    @Transactional
    public void deletePost(String postId) {
        User currentUser = getCurrentAuthenticatedUser();
        Post postToDelete = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (!postToDelete.getUserId().equals(currentUser.getId()) && !isAdminOrSuperAdmin(currentUser)) {
            throw new UnauthorizedException("User not authorized to delete this post");
        }

        // likeRepository.deleteByPostId(postId);
        // commentRepository.deleteByPostId(postId);

        postRepository.deleteById(postId);
    }

    // --- Lógica para Likes ---

    @Override 
    @Transactional
    public void likePost(String postId) {
        User currentUser = getCurrentAuthenticatedUser();
        Post post = postRepository.findById(postId)
             .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (likeRepository.findByPostIdAndUserId(postId, currentUser.getId()).isPresent()) {
             throw new BadRequestException("User already liked this post");
        }

        Like like = Like.builder()
                .postId(postId)
                .userId(currentUser.getId())
                .createdAt(new Date())
                .build();
        likeRepository.save(like);
    }

    @Override 
    @Transactional
    public void unlikePost(String postId) {
         User currentUser = getCurrentAuthenticatedUser();
         Like like = likeRepository.findByPostIdAndUserId(postId, currentUser.getId())
              .orElseThrow(() -> new ResourceNotFoundException("Like not found for this user and post"));
         likeRepository.delete(like);
    }
}