
package com.look.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.look.dto.CommentRequestDto;
import com.look.dto.CommentResponseDto;
import com.look.entity.Comment;
import com.look.entity.Post;
import com.look.entity.Role;
import com.look.entity.User;
import com.look.exception.ResourceNotFoundException;
import com.look.exception.UnauthorizedException;
import com.look.mapper.CommentMapper;
import com.look.repository.CommentRepository;
import com.look.repository.PostRepository;
import com.look.repository.UserRepository;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service 
public class CommentServiceImpl implements CommentService { 

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentMapper commentMapper;

    // --- Helper Methods  ---
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

    private CommentResponseDto enrichCommentResponse(CommentResponseDto dto) {
        userRepository.findById(dto.getUserId()).ifPresent(author -> {
            dto.setAuthorUsername(author.getUsername());
        });
        return dto;
    }

     // --- Public Service Methods  ---
    
    @Override
    public List<CommentResponseDto> getAllComments() {
        List<Comment> comments = commentRepository.findAllByOrderByCreatedAtAsc();

        return comments.stream()
                .map(commentMapper::commentToCommentResponseDto)
                .map(this::enrichCommentResponse) 
                .collect(Collectors.toList());
    }

    @Override 
    @Transactional
    public CommentResponseDto createComment(String postId, CommentRequestDto commentRequestDto) {
        User currentUser = getCurrentAuthenticatedUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Comment comment = commentMapper.commentRequestDtoToComment(commentRequestDto);
        comment.setPostId(postId);
        comment.setUserId(currentUser.getId());
        comment.setCreatedAt(new Date());

        Comment savedComment = commentRepository.save(comment);
        return enrichCommentResponse(commentMapper.commentToCommentResponseDto(savedComment));
    }

    @Override 
    public List<CommentResponseDto> getCommentsByPostId(String postId) {
        if (!postRepository.existsById(postId)) {
             throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        return comments.stream()
                .map(commentMapper::commentToCommentResponseDto)
                .map(this::enrichCommentResponse)
                .collect(Collectors.toList());
    }

    @Override 
    @Transactional
    public void deleteComment(String commentId) {
        User currentUser = getCurrentAuthenticatedUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getUserId().equals(currentUser.getId()) && !isAdminOrSuperAdmin(currentUser)) {
            throw new UnauthorizedException("User not authorized to delete this comment");
        }

        commentRepository.deleteById(commentId);
    }

    @Override 
    @Transactional
    public CommentResponseDto updateComment(String commentId, CommentRequestDto commentRequestDto) {
         User currentUser = getCurrentAuthenticatedUser();
         Comment existingComment = commentRepository.findById(commentId)
                 .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

         if (!existingComment.getUserId().equals(currentUser.getId())) {
             throw new UnauthorizedException("User not authorized to update this comment");
         }

         commentMapper.updateCommentFromDto(commentRequestDto, existingComment);
         Comment updatedComment = commentRepository.save(existingComment);
         return enrichCommentResponse(commentMapper.commentToCommentResponseDto(updatedComment));
     }
}