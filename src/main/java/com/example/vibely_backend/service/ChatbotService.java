package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.*;
import com.example.vibely_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ChatbotService {

    @Autowired
    private ChatbotRepository chatbotRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatGPTService chatGPTService;

    @Autowired
    private LearningGoalRepository learningGoalRepository;

    @Autowired
    private LearningTreeRepository learningTreeRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private StoryRepository storyRepository;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    private void sendToAllEmitters(String data) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(data));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }

    public Chatbot createChat(String userId, String text, String answer) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        if (answer == null || answer.isEmpty()) {
            answer = chatGPTService.getChatGPTResponse(text);
        }

        Chatbot newChat = new Chatbot();
        newChat.setUser(user);
        newChat.setHistory(List.of(
                new ChatHistory("user", List.of(new ChatHistory.ChatPart(text))),
                new ChatHistory("model", List.of(new ChatHistory.ChatPart(answer)))));
        newChat.setCreatedAt(LocalDateTime.now());
        newChat.setUpdatedAt(LocalDateTime.now());

        return chatbotRepository.save(newChat);
    }

    public String handleMessage(String userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        // Lấy thông tin người dùng để cá nhân hóa
        List<LearningGoal> learningGoals = learningGoalRepository.findByUserOrderByCreatedAtDesc(user);
        LearningTree learningTree = learningTreeRepository.findByUser_Id(userId).orElse(null);
        List<Achievement> achievements = achievementRepository.findByUserId(userId);
        List<Schedule> schedules = scheduleRepository.findByUserId(userId);
        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Story> stories = storyRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // Tạo context cho chatbot
        StringBuilder userContext = new StringBuilder();
        userContext.append("Thông tin người dùng:\n");
        userContext.append("- Username: ").append(user.getUsername()).append("\n");
        userContext.append("- Email: ").append(user.getEmail()).append("\n\n");

        userContext.append("Mục tiêu học tập:\n");
        learningGoals.forEach(goal -> userContext.append("- ").append(goal.getTitle())
                .append(" (").append(goal.isCompleted() ? "Đã hoàn thành" : "Chưa hoàn thành").append(")\n"));

        userContext.append("\nCây học tập:\n");
        userContext.append("- Loại cây: ").append(learningTree != null ? learningTree.getTreeType() : "Chưa có")
                .append("\n");
        userContext.append("- Giai đoạn: ").append(learningTree != null ? learningTree.getGrowthStage() : 0)
                .append("\n");
        userContext.append("- Số mục tiêu đã hoàn thành: ")
                .append(learningTree != null ? learningTree.getCompletedGoalsCount() : 0).append("\n\n");

        userContext.append("Thành tựu:\n");
        achievements.forEach(achievement -> userContext.append("- ").append(achievement.getType())
                .append(": ").append(achievement.getGoalsCompleted()).append(" mục tiêu\n"));

        userContext.append("\nLịch học:\n");
        schedules.forEach(schedule -> userContext.append("- ").append(schedule.getSubject())
                .append(": ").append(schedule.getStartTime())
                .append(" - ").append(schedule.getEndTime()).append("\n"));

        userContext.append("\nBài viết gần đây:\n");
        posts.forEach(post -> userContext.append("- ")
                .append(post.getContent() != null
                        ? post.getContent().substring(0, Math.min(50, post.getContent().length())) + "..."
                        : "")
                .append("\n"));

        userContext.append("\nStory gần đây:\n");
        stories.forEach(story -> userContext.append("- ").append(story.getMediaType())
                .append(": ").append(story.getMediaUrl()).append("\n"));

        // Tạo prompt cho chatbot với context
        String prompt = "Bạn là một trợ lý học tập thông minh của Vibely. " +
                "Dựa vào thông tin người dùng sau đây, hãy đưa ra câu trả lời phù hợp và cá nhân hóa:\n\n" +
                userContext.toString() + "\nNgười dùng: " + message + "\nTrợ lý:";

        // Gọi ChatGPT API với context
        String response = chatGPTService.getChatGPTResponse(prompt);

        // Lưu chat vào database
        Chatbot newChat = new Chatbot();
        newChat.setUser(user);
        newChat.setHistory(List.of(
                new ChatHistory("user", List.of(new ChatHistory.ChatPart(message))),
                new ChatHistory("model", List.of(new ChatHistory.ChatPart(response)))));
        newChat.setCreatedAt(LocalDateTime.now());
        newChat.setUpdatedAt(LocalDateTime.now());
        chatbotRepository.save(newChat);

        return response;
    }

    public List<Chatbot> getChats(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        return chatbotRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Chatbot getChatItem(String userId, String chatId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        return chatbotRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc hội thoại"));
    }

    @Transactional
    public Chatbot putQuestion(String userId, String chatId, String question, String answer, String img) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Chatbot chat = chatbotRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc hội thoại"));

        if (!chat.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Không có quyền cập nhật cuộc hội thoại này");
        }

        List<ChatHistory> newItems = new java.util.ArrayList<>();
        if (question != null && !question.isEmpty()) {
            newItems.add(new ChatHistory("user", List.of(new ChatHistory.ChatPart(question))));

            if (answer == null || answer.isEmpty()) {
                answer = chatGPTService.getChatGPTResponse(question);
            }
        }
        newItems.add(new ChatHistory("model", List.of(new ChatHistory.ChatPart(answer))));

        chat.getHistory().addAll(newItems);
        chat.setUpdatedAt(LocalDateTime.now());
        return chatbotRepository.save(chat);
    }

    @Transactional
    public void deleteChatHistory(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        chatbotRepository.deleteByUser(user);
    }

    public List<Chatbot> getChatHistory(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        return chatbotRepository.findByUserOrderByCreatedAtDesc(user);
    }
}