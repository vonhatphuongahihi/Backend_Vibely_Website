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

        @Autowired
        private DocumentRepository documentRepository;

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
                newChat.setUserId(user.getId());
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

                // Xử lý câu hỏi về lịch học
                if (message != null && (message.toLowerCase().contains("lịch học") ||
                                message.toLowerCase().contains("schedule") ||
                                message.toLowerCase().contains("thời khóa biểu"))) {

                        List<Schedule> schedules = scheduleRepository.findByUserIdOrderByStartTimeAsc(userId);
                        if (schedules.isEmpty()) {
                                return "Bạn chưa có lịch học nào. Hãy tạo lịch học để quản lý thời gian hiệu quả!";
                        }

                        StringBuilder response = new StringBuilder("Lịch học của bạn:\n");
                        for (Schedule schedule : schedules) {
                                response.append(String.format(
                                                "- Môn: %s\n" +
                                                                "  Thời gian: %s đến %s\n",
                                                schedule.getSubject(),
                                                schedule.getStartTime(),
                                                schedule.getEndTime()));
                        }
                        return response.toString();
                }

                // Xử lý câu hỏi về cây học tập
                if (message != null && (message.toLowerCase().contains("cây học tập") ||
                                message.toLowerCase().contains("learning tree"))) {
                        List<LearningGoal> goals = learningGoalRepository.findByUserIdOrderByCreatedAtDesc(userId);
                        if (goals.isEmpty()) {
                                return "Bạn chưa có cây học tập. Hãy bắt đầu hành trình học tập của bạn!";
                        }

                        // Lấy thông tin về mục tiêu đã hoàn thành
                        List<LearningGoal> completedGoals = goals.stream()
                                        .filter(LearningGoal::isCompleted)
                                        .collect(Collectors.toList());
                        int totalGoals = goals.size();
                        int completedCount = completedGoals.size();

                        // Lấy thông tin về level hiện tại
                        String currentLevel = "Tân Binh";
                        if (completedCount >= 100) {
                                currentLevel = "Thần Vương";
                        } else if (completedCount >= 50) {
                                currentLevel = "Cao Thủ";
                        } else if (completedCount >= 20) {
                                currentLevel = "Tinh Anh";
                        } else if (completedCount >= 10) {
                                currentLevel = "Chiến Binh";
                        } else if (completedCount >= 5) {
                                currentLevel = "Tập Sự";
                        }

                        return String.format(
                                        "Cây học tập của bạn đang ở level %s. " +
                                                        "Bạn đã hoàn thành %d/%d mục tiêu học tập. " +
                                                        "Mục tiêu gần nhất hoàn thành là: '%s'",
                                        currentLevel,
                                        completedCount,
                                        totalGoals,
                                        completedGoals.isEmpty() ? "Chưa có" : completedGoals.get(0).getTitle());
                }

                // Xử lý câu hỏi về thông tin cá nhân
                if (message != null && (message.toLowerCase().contains("thông tin") ||
                                message.toLowerCase().contains("profile") ||
                                message.toLowerCase().contains("tôi là ai"))) {
                        return String.format(
                                        "Thông tin của bạn:\n" +
                                                        "- Tên người dùng: %s\n" +
                                                        "- Email: %s\n" +
                                                        "- Số bài viết: %d\n" +
                                                        "- Số người theo dõi: %d\n" +
                                                        "- Số người đang theo dõi: %d",
                                        user.getUsername(),
                                        user.getEmail(),
                                        user.getPostsCount(),
                                        user.getFollowerCount(),
                                        user.getFollowingCount());
                }

                // Trước tiên, tìm kiếm trong training data
                List<ChatbotTrainingData> allTrainingData = trainingDataRepository.findAll();
                String bestMatch = findBestMatch(message, allTrainingData);
                if (bestMatch != null) {
                        // Lưu lịch sử chat
                        saveChat(user, message, bestMatch);
                        return bestMatch;
                }

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

                        List<LearningGoal> goals = learningGoalRepository.findByUserIdOrderByCreatedAtDesc(userId);
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

                        List<String> savedDocIds = user.getSavedDocuments();
                        if (savedDocIds == null || savedDocIds.isEmpty()) {
                                return "Bạn chưa lưu tài liệu nào.";
                        }

                        List<DocumentUser> documents = documentRepository.findAllById(savedDocIds);
                        if (documents.isEmpty()) {
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
                newChat.setUserId(user.getId());
                newChat.setHistory(List.of(
                                new ChatHistory("user", List.of(new ChatHistory.ChatPart(message))),
                                new ChatHistory("model", List.of(new ChatHistory.ChatPart(response)))));
                newChat.setCreatedAt(LocalDateTime.now());
                newChat.setUpdatedAt(LocalDateTime.now());
                chatbotRepository.save(newChat);
        }

        public List<Chatbot> getChats(String userId) {
                return chatbotRepository.findByUserIdOrderByCreatedAtDesc(userId);
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

                if (!chat.getUserId().equals(userId)) {
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
                chatbotRepository.deleteByUserId(userId);
        }

        public List<Chatbot> getChatHistory(String userId) {
                List<Chatbot> chats = chatbotRepository.findByUserIdOrderByCreatedAtDesc(userId);
                // Đảo ngược thứ tự tin nhắn trong mỗi chat để tin nhắn mới nhất hiển thị cuối
                // cùng
                chats.forEach(chat -> {
                        if (chat.getHistory() != null) {
                                List<ChatHistory> history = chat.getHistory();
                                List<ChatHistory> orderedHistory = new ArrayList<>();
                                for (int i = 0; i < history.size(); i += 2) {
                                        if (i + 1 < history.size()) {
                                                orderedHistory.add(history.get(i));
                                                orderedHistory.add(history.get(i + 1));
                                        }
                                }
                                chat.setHistory(orderedHistory);
                        }
                });
                return chats;
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

        private String findBestMatch(String message, List<ChatbotTrainingData> matches) {
                if (matches == null || matches.isEmpty()) {
                        return null;
                }

                // Chuẩn hóa message
                String normalizedMessage = message.toLowerCase().trim();

                // Tìm kiếm chính xác
                for (ChatbotTrainingData match : matches) {
                        if (match.getQuestion().toLowerCase().trim().equals(normalizedMessage)) {
                                return match.getAnswer();
                        }
                }

                // Tìm kiếm theo từ khóa
                List<String> messageKeywords = extractKeywords(normalizedMessage);
                int maxKeywordMatches = 0;
                ChatbotTrainingData bestMatch = null;

                for (ChatbotTrainingData match : matches) {
                        List<String> matchKeywords = match.getKeywords();
                        if (matchKeywords != null) {
                                int keywordMatches = 0;
                                for (String keyword : messageKeywords) {
                                        if (matchKeywords.contains(keyword)) {
                                                keywordMatches++;
                                        }
                                }
                                if (keywordMatches > maxKeywordMatches) {
                                        maxKeywordMatches = keywordMatches;
                                        bestMatch = match;
                                }
                        }
                }

                // Nếu có ít nhất 2 từ khóa khớp, trả về câu trả lời
                if (maxKeywordMatches >= 2 && bestMatch != null) {
                        return bestMatch.getAnswer();
                }

                // Nếu không tìm thấy kết quả tốt, sử dụng Levenshtein distance
                return matches.stream()
                                .min(Comparator.comparingInt(match -> calculateLevenshteinDistance(
                                                normalizedMessage, match.getQuestion().toLowerCase())))
                                .filter(match -> calculateLevenshteinDistance(normalizedMessage,
                                                match.getQuestion().toLowerCase()) < 10)
                                .map(ChatbotTrainingData::getAnswer)
                                .orElse(null);
        }

        private List<String> extractKeywords(String message) {
                // Loại bỏ các từ không cần thiết
                String[] stopWords = { "là", "gì", "có", "không", "để", "làm", "sao", "thế", "nào", "cho", "tôi", "bạn",
                                "của", "và", "hoặc", "với", "trong", "ngoài", "trên", "dưới", "trước", "sau" };
                List<String> keywords = new ArrayList<>();

                // Tách từ và lọc stop words
                String[] words = message.split("\\s+");
                for (String word : words) {
                        word = word.toLowerCase().trim();
                        if (!Arrays.asList(stopWords).contains(word) && word.length() > 1) {
                                keywords.add(word);
                        }
                }

                return keywords;
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