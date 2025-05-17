package com.example.vibely_backend.service;

import com.example.vibely_backend.dto.response.StoryDTO;
import com.example.vibely_backend.dto.response.UserMiniDTO;
import com.example.vibely_backend.entity.Story;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.StoryRepository;
import com.example.vibely_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    
    public List<StoryDTO> getAllStories() {
        List<Story> stories = storyRepository.findAll();
        return stories.stream().map(story -> {
            User storyUser = story.getUser();
            UserMiniDTO userDTO = storyUser != null ? new UserMiniDTO(storyUser) : null;
            return new StoryDTO(story, userDTO);
        }).collect(Collectors.toList());
    }

    public StoryDTO getStoryById(String storyId) {
        return storyRepository.findById(storyId).map(story -> {
            UserMiniDTO userDTO = null;
            User storyUser = story.getUser();
            
            // If we have a user reference, try to get the full user object
            if (storyUser != null) {
                User fullUser = userRepository.findByEmail(storyUser.getEmail())
                        .orElse(null);
                if (fullUser != null) {
                    userDTO = new UserMiniDTO(fullUser);
                }
            }

            return new StoryDTO(story, userDTO);
        }).orElse(null);
    }
    
    public List<StoryDTO> getStoriesByUserId(String userId) {
        List<Story> stories = storyRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return stories.stream().map(story -> {
            User storyUser = story.getUser();
            UserMiniDTO userDTO = storyUser != null ? new UserMiniDTO(storyUser) : null;
            return new StoryDTO(story, userDTO);
        }).collect(Collectors.toList());
    }
} 