package com.example.vibely_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.vibely_backend.dto.response.MutualFriendResponse;
import com.example.vibely_backend.dto.response.SimpleUserResponse;
import com.example.vibely_backend.dto.response.UserProfileResponse;
import com.example.vibely_backend.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findByUsernameIgnoreCaseContainingOrEmailIgnoreCaseContaining(String username, String email);

    List<User> findAllByEmail(String email);

    @Query("SELECT new com.example.vibely_backend.dto.response.SimpleUserResponse(u.id, u.username, u.profilePicture) FROM User u")
    List<SimpleUserResponse> findAllSimpleUsers();

    @Query("SELECT new com.example.vibely_backend.dto.response.MutualFriendResponse(u.id, u.username, u.profilePicture, u.email, u.followerCount, u.followingCount) FROM User u")
    List<MutualFriendResponse> findAllUserFull();

    @Query("SELECT new com.example.vibely_backend.dto.response.UserProfileResponse(" +
            "u.id, u.username, u.email, u.gender, u.dateOfBirth, u.profilePicture, u.coverPicture, " +
            "new com.example.vibely_backend.dto.response.UserProfileResponse$Bio(" +
            "b.bioText, b.liveIn, b.relationship, b.workplace, b.education, b.phone, b.hometown)) " +
            "FROM User u LEFT JOIN Bio b ON u.id = b.user.id WHERE u.id = :userId")
    Optional<UserProfileResponse> findUserProfileById(@Param("userId") String userId);
}