package com.look.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import com.look.dto.PostRequestDto;
import com.look.dto.PostResponseDto;
import com.look.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "Posts", description = "Operations related to posts")
@SecurityRequirement(name = "bearerAuth")
public class PostController {

    @Autowired
    PostService postService;

    @Operation(summary = "Create a new post", description = "Requires authentication (USER, ADMIN, SUPERADMIN)")
    @ApiResponse(responseCode = "201", description = "Post created successfully", content = @Content(schema = @Schema(implementation = PostResponseDto.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ApiResponseDto<PostResponseDto>> createPost(
            @Valid @RequestBody PostRequestDto postRequestDto) {
        PostResponseDto createdPost = postService.createPost(postRequestDto);
        ApiResponseDto<PostResponseDto> response = new ApiResponseDto<>("Post created successfully",
                HttpStatus.CREATED.value(), createdPost);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all posts", description = "Public endpoint")
    @ApiResponse(responseCode = "200", description = "Posts fetched successfully")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<List<PostResponseDto>>> getAllPosts() {
        List<PostResponseDto> posts = postService.getAllPosts();
        ApiResponseDto<List<PostResponseDto>> response = new ApiResponseDto<>("Posts fetched successfully",
                HttpStatus.OK.value(), posts);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get post by ID", description = "Public endpoint")
    @ApiResponse(responseCode = "200", description = "Post fetched successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @GetMapping(value = "/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDto<PostResponseDto>> getPostById(
            @Parameter(description = "ID of the post to retrieve", required = true, in = ParameterIn.PATH) @PathVariable String postId) {
        PostResponseDto post = postService.getPostById(postId);
        ApiResponseDto<PostResponseDto> response = new ApiResponseDto<>("Post fetched successfully",
                HttpStatus.OK.value(), post);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update a post", description = "Requires authentication. Only author or ADMIN/SUPERADMIN.")
    @ApiResponse(responseCode = "200", description = "Post updated successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to update this specific post")
    @PutMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<ApiResponseDto<PostResponseDto>> updatePost(
            @Parameter(description = "ID of the post to update", required = true) @PathVariable String postId,
            @Valid @RequestBody PostRequestDto postRequestDto) {
        PostResponseDto updatedPost = postService.updatePost(postId, postRequestDto);
        ApiResponseDto<PostResponseDto> response = new ApiResponseDto<>("Post updated successfully",
                HttpStatus.OK.value(), updatedPost);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a post", description = "Requires authentication. Only author or ADMIN/SUPERADMIN.")
    @ApiResponse(responseCode = "204", description = "Post deleted successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to delete this specific post")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "ID of the post to delete", required = true) @PathVariable String postId) {
        postService.deletePost(postId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Like a post", description = "Requires authentication")
    @ApiResponse(responseCode = "200", description = "Post liked successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @ApiResponse(responseCode = "400", description = "User already liked this post")
    @PostMapping("/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<Void>> likePost(
            @Parameter(description = "ID of the post to like", required = true) @PathVariable String postId) {
        postService.likePost(postId);
        ApiResponseDto<Void> response = new ApiResponseDto<>("Post liked successfully", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Unlike a post", description = "Requires authentication")
    @ApiResponse(responseCode = "200", description = "Post unliked successfully")
    @ApiResponse(responseCode = "404", description = "Post or Like not found")
    @DeleteMapping("/{postId}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseDto<Void>> unlikePost(
            @Parameter(description = "ID of the post to unlike", required = true) @PathVariable String postId) {
        postService.unlikePost(postId);
        ApiResponseDto<Void> response = new ApiResponseDto<>("Post unliked successfully", HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

}