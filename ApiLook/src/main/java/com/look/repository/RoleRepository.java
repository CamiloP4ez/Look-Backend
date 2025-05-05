package com.look.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.look.entity.Role;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
    Set<Role> findByNameIn(Set<String> names);
}