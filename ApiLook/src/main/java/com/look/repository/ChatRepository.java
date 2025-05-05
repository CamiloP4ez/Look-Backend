package com.look.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.look.entity.Chat;

import java.util.Optional;
import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    Optional<Chat> findByUser1IdAndUser2IdOrUser2IdAndUser1Id(String userId1, String userId2, String userId1Again, String userId2Again);

    List<Chat> findByUser1IdOrUser2Id(String userId1, String userId2);
}