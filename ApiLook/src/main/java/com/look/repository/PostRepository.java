package com.look.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.look.entity.Post;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    @Query("{ 'user_id' : ?0 }")
    List<Post> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Post> findByUserIdInOrderByCreatedAtDesc(Collection<String> userIds);
}