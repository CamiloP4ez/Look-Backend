package com.look.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.look.dto.ApiResponseDto;
import com.look.dto.CommentRequestDto;
import com.look.dto.CommentResponseDto;
import com.look.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Comments", description = "Operations related to comments")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    @Autowired
    CommentService commentService;

    @Operation(summary = "Create a comment on a post", description = "Requires authentication.")
    @PostMapping(value = "/posts/{postId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> createComment(
            @Parameter(description = "ID of the post to comment on", required = true) @PathVariable String postId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {

        CommentResponseDto createdComment = commentService.createComment(postId, commentRequestDto);
        ApiResponseDto<CommentResponseDto> response = new ApiResponseDto<>("Comment created successfully",
                HttpStatus.CREATED.value(), createdComment);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all comments for a post", description = "Public endpoint.")
    @GetMapping(value = "/posts/{postId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<ApiResponseDto<List<CommentResponseDto>>> getCommentsByPostId(
            @Parameter(description = "ID of the post whose comments are to be retrieved", required = true) @PathVariable String postId) {
        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);
        ApiResponseDto<List<CommentResponseDto>> response = new ApiResponseDto<>("Comments fetched successfully",
                HttpStatus.OK.value(), comments);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a comment", description = "Requires authentication. Only author or ADMIN/SUPERADMIN.")
    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID of the comment to delete", required = true) @PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a comment", description = "Requires authentication. Only the author can update.")
    @PutMapping(value = "/comments/{commentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<CommentResponseDto>> updateComment(
            @Parameter(description = "ID of the comment to update", required = true) @PathVariable String commentId,
            @Valid @RequestBody CommentRequestDto commentRequestDto) {
        CommentResponseDto updatedComment = commentService.updateComment(commentId, commentRequestDto);
        ApiResponseDto<CommentResponseDto> response = new ApiResponseDto<>("Comment updated successfully",
                HttpStatus.OK.value(), updatedComment);
        return ResponseEntity.ok(response);
    }

}