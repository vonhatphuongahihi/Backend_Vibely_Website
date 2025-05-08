package com.example.vibely_backend.service;

import com.example.vibely_backend.dto.response.PostDTO;
import com.example.vibely_backend.dto.response.UserMiniDTO;
import com.example.vibely_backend.entity.Post;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.PostRepository;
import com.example.vibely_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    public List<PostDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream().map(post -> {
            UserMiniDTO userDTO = null;
            
            User postUser = post.getUser();
            System.out.println( "postUser: " + postUser + " post: " + post);
            if (postUser != null && postUser.getId() != null) {
                userDTO = userRepository.findByEmail(postUser.getId())
                        .map(UserMiniDTO::new)
                        .orElse(null);
            }

            return new PostDTO(post, userDTO);
        }).collect(Collectors.toList());
    }

    public PostDTO getPostById(String postId) {
        return postRepository.findById(postId).map(post -> {
            UserMiniDTO userDTO = null;
            if (post.getUser() != null && post.getUser().getId() != null) {
                userDTO = userRepository.findById(post.getUser().getId())
                        .map(UserMiniDTO::new)
                        .orElse(null);
            }
            return new PostDTO(post, userDTO);
        }).orElse(null);
    }

}
