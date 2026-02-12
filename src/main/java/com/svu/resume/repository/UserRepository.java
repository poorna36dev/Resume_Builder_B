package com.svu.resume.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.svu.resume.document.User;
@Repository
public interface UserRepository extends MongoRepository<User,String>{
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User>findByVerficationToken(String verficationToken);

    Optional<User> findById(String userId);
}
