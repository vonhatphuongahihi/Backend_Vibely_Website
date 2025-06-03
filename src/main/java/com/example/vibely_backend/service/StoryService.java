package com.example.vibely_backend.service;

import com.example.vibely_backend.dto.response.StoryDTO;
import com.example.vibely_backend.dto.response.UserMiniDTO;
import com.example.vibely_backend.entity.Story;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.StoryRepository;
import com.example.vibely_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    public List<StoryDTO> getAllStories() {
        List<Story> stories = storyRepository.findAll();
        return stories.stream().map(story -> {
            UserMiniDTO userDTO = null;
            if (story.getUserId() != null) {
                User storyUser = userRepository.findById(story.getUserId()).orElse(null);
                userDTO = storyUser != null ? new UserMiniDTO(storyUser) : null;
            }

            // Ensure createdAt is not null
            if (story.getCreatedAt() == null) {
                story.setCreatedAt(new Date());
            }
            if (story.getUpdatedAt() == null) {
                story.setUpdatedAt(new Date());
            }

            return new StoryDTO(story, userDTO);
        }).collect(Collectors.toList());
    }

    public StoryDTO getStoryById(String storyId) {
        return storyRepository.findById(storyId).map(story -> {
            UserMiniDTO userDTO = null;
            User storyUser = userRepository.findById(story.getUserId()).orElse(null);

            if (storyUser != null) {
                userDTO = new UserMiniDTO(storyUser);
            }

            return new StoryDTO(story, userDTO);
        }).orElse(null);
    }

    public List<StoryDTO> getStoriesByUserId(String userId) {
        List<Story> stories = storyRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return stories.stream().map(story -> {
            User storyUser = userRepository.findById(story.getUserId()).orElse(null);
            UserMiniDTO userDTO = storyUser != null ? new UserMiniDTO(storyUser) : null;
            return new StoryDTO(story, userDTO);
        }).collect(Collectors.toList());
    }
}