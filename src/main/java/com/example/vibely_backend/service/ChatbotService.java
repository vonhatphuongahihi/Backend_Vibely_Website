package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.*;
import com.example.vibely_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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

        @Autowired
        private ChatbotTrainingDataRepository trainingDataRepository;

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
                // Lấy thông tin người dùng
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Xử lý câu hỏi về bài viết của người dùng
                if (message != null && (message.toLowerCase().contains("đăng bài") ||
                                message.toLowerCase().contains("bài viết") ||
                                message.toLowerCase().contains("post"))) {

                        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);
                        if (posts.isEmpty()) {
                                return "Bạn chưa đăng bài viết nào.";
                        }

                        if (posts.size() == 1) {
                                Post post = posts.get(0);
                                return String.format("Bạn đã đăng 1 bài viết: '%s' vào lúc %s",
                                                post.getContent(),
                                                post.getCreatedAt());
                        } else {
                                return String.format("Bạn đã đăng %d bài viết. Bài viết gần nhất là: '%s' vào lúc %s",
                                                posts.size(),
                                                posts.get(0).getContent(),
                                                posts.get(0).getCreatedAt());
                        }
                }

                // Xử lý câu hỏi về mục tiêu học tập
                if (message != null && (message.toLowerCase().contains("mục tiêu") ||
                                message.toLowerCase().contains("learning goal"))) {

                        List<LearningGoal> goals = learningGoalRepository.findByUserOrderByCreatedAtDesc(user);
                        if (goals.isEmpty()) {
                                return "Bạn chưa đặt mục tiêu học tập nào.";
                        }

                        return String.format("Bạn có %d mục tiêu học tập. Mục tiêu gần nhất là: '%s'",
                                        goals.size(),
                                        goals.get(0).getTitle());
                }

                // Xử lý câu hỏi về thành tựu
                if (message != null && (message.toLowerCase().contains("thành tựu") ||
                                message.toLowerCase().contains("achievement"))) {

                        List<Achievement> achievements = achievementRepository.findByUserId(userId);
                        if (achievements.isEmpty()) {
                                return "Bạn chưa đạt được thành tựu nào.";
                        }

                        return String.format("Bạn đã đạt được %d thành tựu. Thành tựu gần nhất là: '%s'",
                                        achievements.size(),
                                        achievements.get(0).getDetails().getTitle());
                }

                // Xử lý câu hỏi về tài liệu
                if (message != null && (message.toLowerCase().contains("tài liệu") ||
                                message.toLowerCase().contains("document"))) {

                        List<DocumentUser> documents = user.getSavedDocuments();
                        if (documents == null || documents.isEmpty()) {
                                return "Bạn chưa lưu tài liệu nào.";
                        }

                        return String.format("Bạn đã lưu %d tài liệu. Tài liệu gần nhất là: '%s'",
                                        documents.size(),
                                        documents.get(0).getTitle());
                }

                // Tạo prompt với thông tin người dùng
                String prompt = String.format(
                                "Người dùng %s (email: %s) đang hỏi: %s. " +
                                                "Hãy trả lời dựa trên ngữ cảnh của câu hỏi và thông tin người dùng. " +
                                                "Nếu câu hỏi liên quan đến thông tin cá nhân, hãy trả lời dựa trên dữ liệu thực tế của người dùng.",
                                user.getUsername(),
                                user.getEmail(),
                                message);

                String response = chatGPTService.getChatGPTResponse(prompt);

                // Lưu lịch sử chat
                saveChat(user, message, response);

                return response;
        }

        private void saveChat(User user, String message, String response) {
                Chatbot newChat = new Chatbot();
                newChat.setUser(user);
                newChat.setHistory(List.of(
                                new ChatHistory("user", List.of(new ChatHistory.ChatPart(message))),
                                new ChatHistory("model", List.of(new ChatHistory.ChatPart(response)))));
                newChat.setCreatedAt(LocalDateTime.now());
                newChat.setUpdatedAt(LocalDateTime.now());
                chatbotRepository.save(newChat);
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
        public Chatbot putQuestion(String userId, String chatId, String question, String answer) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

                Chatbot chat = chatbotRepository.findById(chatId)
                                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cuộc hội thoại"));

                if (!chat.getUser().getId().equals(userId)) {
                        throw new IllegalArgumentException("Không có quyền cập nhật cuộc hội thoại này");
                }

                List<ChatHistory> newItems = new ArrayList<>();
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

        // Thêm các phương thức quản lý training data
        public ChatbotTrainingData addTrainingData(String question, String answer,
                        String category, List<String> keywords) {
                ChatbotTrainingData data = new ChatbotTrainingData();
                data.setQuestion(question);
                data.setAnswer(answer);
                data.setCategory(category);
                data.setKeywords(keywords);
                data.setCreatedAt(LocalDateTime.now());
                data.setUpdatedAt(LocalDateTime.now());

                return trainingDataRepository.save(data);
        }

        public List<ChatbotTrainingData> getTrainingDataByCategory(String category) {
                return trainingDataRepository.findByCategory(category);
        }

        private List<String> extractKeywords(String message) {
                // Implement logic để trích xuất từ khóa từ message
                // Có thể sử dụng thư viện NLP như Stanford NLP hoặc OpenNLP
                return Arrays.asList(message.toLowerCase().split("\\s+"));
        }

        private String findBestMatch(String message, List<ChatbotTrainingData> matches) {
                // Implement thuật toán Levenshtein distance để tìm câu trả lời phù hợp nhất
                return matches.stream()
                                .min(Comparator.comparingInt(match -> calculateLevenshteinDistance(
                                                message.toLowerCase(), match.getQuestion().toLowerCase())))
                                .map(ChatbotTrainingData::getAnswer)
                                .orElse(null);
        }

        private int calculateLevenshteinDistance(String s1, String s2) {
                int[][] dp = new int[s1.length() + 1][s2.length() + 1];

                for (int i = 0; i <= s1.length(); i++) {
                        dp[i][0] = i;
                }
                for (int j = 0; j <= s2.length(); j++) {
                        dp[0][j] = j;
                }

                for (int i = 1; i <= s1.length(); i++) {
                        for (int j = 1; j <= s2.length(); j++) {
                                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                                        dp[i][j] = dp[i - 1][j - 1];
                                } else {
                                        dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                                }
                        }
                }

                return dp[s1.length()][s2.length()];
        }
}