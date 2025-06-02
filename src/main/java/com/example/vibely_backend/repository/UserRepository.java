package com.example.vibely_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.entity.Provider;
import com.example.vibely_backend.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndProvider(String email, Provider provider);

    Optional<User> findByUsername(String username);

    List<User> findByUsernameIgnoreCaseContainingOrEmailIgnoreCaseContaining(String username, String email);

    List<User> findAllByEmail(String email);

    @Query(value = "{}", fields = "{ 'id': 1, 'username': 1, 'profilePicture': 1 }")
    List<User> findAllSimpleUsers();

    @Query(value = "{}", fields = "{ 'id': 1, 'username': 1, 'profilePicture': 1, 'email': 1, 'followerCount': 1, 'followingCount': 1 }")
    List<User> findAllUserFull();

    @Query(value = "{ '_id': ?0 }", fields = "{ 'id': 1, 'username': 1, 'email': 1, 'gender': 1, 'dateOfBirth': 1, 'profilePicture': 1, 'coverPicture': 1, 'bioId': 1 }")
    Optional<User> findUserProfileById(String userId);
}