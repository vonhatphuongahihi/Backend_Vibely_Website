package com.example.vibely_backend.service;

import com.example.vibely_backend.entity.*;
import com.example.vibely_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ChatbotTrainingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LearningGoalRepository learningGoalRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ChatbotService chatbotService;

    public void generateDynamicTrainingData() {
        // Training data cho Message feature
        chatbotService.addTrainingData(
                "Tôi có thể nhắn tin trực tuyến với bạn bè qua Vibely không?",
                "Có, Vibely có tính năng Message cho phép bạn nhắn tin riêng tư với bạn bè. Bạn có thể trao đổi thông tin, chia sẻ tài liệu học tập, hoặc thảo luận về bài học với bạn bè một cách dễ dàng.",
                "feature-message",
                new ArrayList<>());

        chatbotService.addTrainingData(
                "Làm sao để nhắn tin với bạn bè?",
                "Để nhắn tin với bạn bè: 1) Vào phần Message, 2) Chọn người bạn muốn nhắn tin, 3) Nhập nội dung tin nhắn, 4) Có thể đính kèm tài liệu học tập, 5) Nhấn gửi để hoàn tất.",
                "feature-message",
                new ArrayList<>());

        chatbotService.addTrainingData(
                "Tôi có thể chia sẻ tài liệu qua tin nhắn không?",
                "Có, bạn có thể chia sẻ tài liệu học tập qua tin nhắn. Khi nhắn tin, bạn có thể đính kèm các file PDF, Word, hoặc hình ảnh để chia sẻ với bạn bè.",
                "feature-message",
                new ArrayList<>());

        // Training data cho Learning Goals
        List<LearningGoal> goals = learningGoalRepository.findAll();
        goals.forEach(goal -> {
            chatbotService.addTrainingData(
                    "Mục tiêu học tập của tôi là gì?",
                    "Bạn có mục tiêu học tập: " + goal.getTitle() +
                            ". Trạng thái: " + (goal.isCompleted() ? "Đã hoàn thành" : "Chưa hoàn thành") +
                            ". Thời gian tạo: " + goal.getCreatedAt(),
                    "user-data",
                    new ArrayList<>());
        });

        // Training data cho Achievements
        List<Achievement> achievements = achievementRepository.findAll();
        achievements.forEach(achievement -> {
            Achievement.AchievementDetails details = achievement.getDetails();
            chatbotService.addTrainingData(
                    "Thành tựu của tôi là gì?",
                    "Bạn đã đạt được thành tựu: " + details.getTitle() +
                            ". Mô tả: " + details.getDescription() +
                            ". Loại: " + achievement.getType() +
                            ". Số mục tiêu đã hoàn thành: " + achievement.getGoalsCompleted(),
                    "user-data",
                    new ArrayList<>());
        });

        // Training data cho Subjects
        List<Subject> subjects = subjectRepository.findAll();
        subjects.forEach(subject -> {
            chatbotService.addTrainingData(
                    "Môn học của tôi là gì?",
                    "Bạn đang học môn: " + subject.getName() +
                            ". Level: " + subject.getLevel().getName() +
                            ". Thời gian tạo: " + subject.getCreatedAt(),
                    "user-data",
                    new ArrayList<>());
        });

        // Training data cho Documents
        List<DocumentUser> documents = documentRepository.findAll();
        documents.forEach(doc -> {
            chatbotService.addTrainingData(
                    "Tài liệu học tập của tôi là gì?",
                    "Bạn có tài liệu: " + doc.getTitle() +
                            ". Loại file: " + doc.getFileType() +
                            ". Số trang: " + doc.getPages() +
                            ". Môn học: " + doc.getSubject().getName() +
                            ". Level: " + doc.getLevel().getName(),
                    "user-data",
                    new ArrayList<>());
        });

        // Training data cho User
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            // Thông tin cơ bản
            chatbotService.addTrainingData(
                    "Thông tin của tôi là gì?",
                    "Tên người dùng: " + user.getUsername() +
                            ". Email: " + user.getEmail() +
                            ". Số bài viết: " + user.getPostsCount() +
                            ". Số người theo dõi: " + user.getFollowerCount() +
                            ". Số người đang theo dõi: " + user.getFollowingCount(),
                    "user-data",
                    new ArrayList<>());

            // Thông tin về hoạt động của người dùng cụ thể
            chatbotService.addTrainingData(
                    user.getUsername() + " làm gì?",
                    user.getUsername() + " đã: " +
                            (user.getPostsCount() > 0 ? "đăng " + user.getPostsCount() + " bài viết, " : "") +
                            (user.getFollowerCount() > 0 ? "có " + user.getFollowerCount() + " người theo dõi, " : "") +
                            (user.getFollowingCount() > 0 ? "đang theo dõi " + user.getFollowingCount() + " người, "
                                    : "")
                            +
                            (user.getSavedDocuments() != null && !user.getSavedDocuments().isEmpty()
                                    ? "lưu " + user.getSavedDocuments().size() + " tài liệu học tập"
                                    : ""),
                    "user-activity",
                    new ArrayList<>());

            // Thông tin về bài viết
            if (user.getPosts() != null && !user.getPosts().isEmpty()) {
                user.getPosts().forEach(post -> {
                    String reactionInfo = "";
                    if (post.getReactionStats() != null) {
                        Post.ReactionStats stats = post.getReactionStats();
                        reactionInfo = String.format(
                                ". Số lượt thích: %d, yêu thích: %d, haha: %d, wow: %d, buồn: %d, giận: %d",
                                stats.getLike(), stats.getLove(), stats.getHaha(),
                                stats.getWow(), stats.getSad(), stats.getAngry());
                    }

                    chatbotService.addTrainingData(
                            "Bài viết của tôi là gì?",
                            "Bài viết: " + post.getContent() +
                                    (post.getMediaUrl() != null ? ". Có đính kèm " + post.getMediaType() : "") +
                                    ". Số bình luận: " + post.getCommentCount() +
                                    ". Số lượt chia sẻ: " + post.getShareCount() +
                                    reactionInfo +
                                    ". Thời gian đăng: " + post.getCreatedAt(),
                            "user-data",
                            new ArrayList<>());
                });
            }

            // Thông tin về tài liệu đã lưu
            if (user.getSavedDocuments() != null && !user.getSavedDocuments().isEmpty()) {
                user.getSavedDocuments().forEach(doc -> {
                    chatbotService.addTrainingData(
                            "Tài liệu đã lưu của tôi là gì?",
                            "Tài liệu: " + doc.getTitle() +
                                    ". Loại file: " + doc.getFileType() +
                                    ". Môn học: " + doc.getSubject().getName(),
                            "user-data",
                            new ArrayList<>());
                });
            }
        });
    }
}