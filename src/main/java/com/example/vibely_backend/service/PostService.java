package com.example.vibely_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.vibely_backend.dto.response.PostDTO;
import com.example.vibely_backend.dto.response.UserMiniDTO;
import com.example.vibely_backend.entity.Post;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.PostRepository;
import com.example.vibely_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(post -> {
            User postUser = userRepository.findById(post.getUserId()).orElse(null);
            UserMiniDTO userDTO = postUser != null ? new UserMiniDTO(postUser) : null;
            return new PostDTO(post, userDTO);
        }).collect(Collectors.toList());
    }

    public PostDTO getPostById(String postId) {
        return postRepository.findById(postId).map(post -> {
            UserMiniDTO userDTO = null;
            User postUser = userRepository.findById(post.getUserId()).orElse(null);

            if (postUser != null) {
                userDTO = new UserMiniDTO(postUser);
            }

            return new PostDTO(post, userDTO);
        }).orElse(null);
    }

    public List<PostDTO> getPostsByUserId(String userId) {
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return posts.stream().map(post -> {
            User postUser = userRepository.findById(post.getUserId()).orElse(null);
            UserMiniDTO userDTO = postUser != null ? new UserMiniDTO(postUser) : null;
            return new PostDTO(post, userDTO);
        }).collect(Collectors.toList());
    }
}
