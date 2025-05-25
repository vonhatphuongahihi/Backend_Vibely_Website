package com.example.vibely_backend.config;

import com.example.vibely_backend.service.ChatbotService;
import com.example.vibely_backend.service.ChatbotTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private ChatbotTrainingService chatbotTrainingService;

    @Override
    public void run(String... args) {
        // Thêm dữ liệu mẫu cố định
        addStaticTrainingData();

        // Thêm dữ liệu động từ database
        chatbotTrainingService.generateDynamicTrainingData();
    }

    private void addStaticTrainingData() {
        // Thêm dữ liệu mẫu về Vibely
        chatbotService.addTrainingData(
                "Vibely là gì?",
                "Vibely là một nền tảng học tập xã hội giúp người dùng theo dõi và chia sẻ quá trình học tập của mình. Bạn có thể đặt mục tiêu học tập, theo dõi tiến độ thông qua Learning Tree, và chia sẻ thành tích với bạn bè.",
                "platform-intro",
                Arrays.asList("platform-introduction", "what-is-vibely"));

        // Thêm dữ liệu về các tính năng
        chatbotService.addTrainingData(
                "Learning Tree là gì?",
                "Learning Tree là tính năng giúp bạn theo dõi tiến độ học tập. Mỗi mục tiêu học tập hoàn thành sẽ giúp cây của bạn phát triển. Bạn có thể thấy được sự tiến bộ của mình qua các giai đoạn phát triển của cây.",
                "feature-learning-tree",
                Arrays.asList("learning-tree-feature", "tree-progress"));

        chatbotService.addTrainingData(
                "Achievements là gì?",
                "Achievements là hệ thống thành tựu trong Vibely. Khi bạn hoàn thành các mục tiêu học tập, bạn sẽ nhận được các thành tựu khác nhau. Điều này giúp khuyến khích và theo dõi sự tiến bộ của bạn.",
                "feature-achievements",
                Arrays.asList("achievements-system", "user-achievements"));

        chatbotService.addTrainingData(
                "Schedule có tác dụng gì?",
                "Schedule giúp bạn lên lịch học tập và quản lý thời gian hiệu quả. Bạn có thể tạo các lịch học cho từng môn học, đặt thời gian bắt đầu và kết thúc, và nhận thông báo khi đến giờ học.",
                "feature-schedule",
                Arrays.asList("schedule-management", "study-schedule"));

        // Thêm dữ liệu về cách sử dụng
        chatbotService.addTrainingData(
                "Làm sao để đặt mục tiêu học tập?",
                "Để đặt mục tiêu học tập, bạn cần: 1) Vào phần Learning Goals, 2) Nhấn nút 'Thêm mục tiêu', 3) Điền thông tin mục tiêu như tiêu đề, mô tả, thời hạn, 4) Nhấn 'Lưu' để hoàn tất.",
                "how-to-learning-goals",
                Arrays.asList("set-learning-goals", "create-goals"));

        chatbotService.addTrainingData(
                "Làm sao để chia sẻ bài viết?",
                "Để chia sẻ bài viết: 1) Vào phần Posts, 2) Nhấn nút 'Tạo bài viết', 3) Viết nội dung bài viết, 4) Thêm hình ảnh nếu muốn, 5) Nhấn 'Đăng' để chia sẻ với mọi người.",
                "how-to-posts",
                Arrays.asList("create-posts", "share-posts"));

        chatbotService.addTrainingData(
                "Làm sao để học hiệu quả hơn trên Vibely?",
                "Bạn có thể học hiệu quả hơn bằng cách: 1) Đặt mục tiêu nhỏ mỗi ngày, 2) Sử dụng tính năng Schedule để nhắc nhở học tập, 3) Chia sẻ tiến trình để có động lực từ bạn bè.",
                "tips-effective-learning",
                Arrays.asList("learning-tips", "productivity"));
                
        // Thêm dữ liệu về Story
        chatbotService.addTrainingData(
                "Story là gì?",
                "Story là tính năng cho phép bạn chia sẻ khoảnh khắc học tập của mình dưới dạng hình ảnh hoặc video. Story sẽ tự động biến mất sau 24 giờ, giúp bạn chia sẻ nhanh chóng và tự nhiên hơn.",
                "feature-story",
                Arrays.asList("story-feature", "moment-sharing"));

        // Thêm dữ liệu về Posts
        chatbotService.addTrainingData(
                "Posts có tác dụng gì?",
                "Posts cho phép bạn chia sẻ bài viết dài về quá trình học tập, kinh nghiệm, hoặc tài liệu học tập. Khác với Story, Posts sẽ được lưu trữ lâu dài và có thể tương tác qua comments.",
                "feature-posts",
                Arrays.asList("posts-feature", "long-form-content"));

        // Thêm dữ liệu về Learning Goals
        chatbotService.addTrainingData(
                "Learning Goals là gì?",
                "Learning Goals là tính năng giúp bạn đặt và theo dõi các mục tiêu học tập. Bạn có thể đặt mục tiêu ngắn hạn hoặc dài hạn, theo dõi tiến độ, và nhận thông báo khi hoàn thành.",
                "feature-learning-goals",
                Arrays.asList("learning-goals-feature", "goal-tracking"));

        // Thêm dữ liệu về Quiz
        chatbotService.addTrainingData(
                "Quiz là gì?",
                "Quiz là tính năng cho phép bạn tạo và tham gia các bài kiểm tra kiến thức. Bạn có thể tạo quiz cho môn học của mình hoặc tham gia quiz của người khác để kiểm tra kiến thức.",
                "feature-quiz",
                Arrays.asList("quiz-feature", "knowledge-test"));

        // Thêm dữ liệu về Subject
        chatbotService.addTrainingData(
                "Subject là gì?",
                "Subject là tính năng giúp bạn quản lý các môn học. Bạn có thể thêm các môn học, theo dõi tiến độ học tập của từng môn, và chia sẻ tài liệu học tập.",
                "feature-subject",
                Arrays.asList("subject-management", "course-tracking"));

        // Thêm dữ liệu về Level
        chatbotService.addTrainingData(
                "Level là gì?",
                "Level là hệ thống cấp độ trong Vibely. Khi bạn hoàn thành các mục tiêu và tích lũy điểm kinh nghiệm, level của bạn sẽ tăng lên. Level cao hơn sẽ mở khóa thêm nhiều tính năng thú vị.",
                "feature-level",
                Arrays.asList("level-system", "experience-points"));

        // Thêm dữ liệu về privacy
        chatbotService.addTrainingData(
                "Chatbot có thể xem thông tin của người khác không?",
                "Không, chatbot chỉ có thể xem và trả lời các thông tin liên quan đến tài khoản của bạn. Thông tin của người khác sẽ được bảo vệ và không được tiết lộ.",
                "privacy-chatbot",
                Arrays.asList("chatbot-privacy", "data-protection"));

        chatbotService.addTrainingData(
                "Vibely có bảo vệ thông tin người dùng không?",
                "Có, Vibely cam kết bảo mật dữ liệu người dùng. Chúng tôi mã hóa thông tin và tuân thủ các quy định về bảo vệ dữ liệu cá nhân.",
                "data-security",
                Arrays.asList("user-data-security", "privacy-policy"));

        chatbotService.addTrainingData(
                "Làm sao để bảo vệ thông tin cá nhân?",
                "Để bảo vệ thông tin cá nhân: 1) Không chia sẻ mật khẩu, 2) Cài đặt quyền riêng tư cho bài viết, 3) Chỉ kết bạn với người quen, 4) Báo cáo nếu thấy thông tin bị lộ.",
                "privacy-protection",
                Arrays.asList("personal-data-protection", "account-security"));

        // Thêm dữ liệu về Document
        chatbotService.addTrainingData(
                "Document là gì?",
                "Document là tính năng cho phép bạn lưu trữ và chia sẻ tài liệu học tập. Bạn có thể upload các file PDF, Word, hoặc hình ảnh và chia sẻ với bạn bè hoặc giữ riêng tư.",
                "feature-document",
                Arrays.asList("document-management", "file-sharing"));

        // Thêm dữ liệu về Message
        chatbotService.addTrainingData(
                "Message là gì?",
                "Message là tính năng nhắn tin riêng tư giữa các người dùng. Bạn có thể trao đổi thông tin, chia sẻ tài liệu, hoặc thảo luận về bài học với bạn bè.",
                "feature-message",
                Arrays.asList("private-messaging", "user-communication"));

        // Thêm dữ liệu về Inquiry
        chatbotService.addTrainingData(
                "Inquiry là gì?",
                "Inquiry là tính năng cho phép bạn đặt câu hỏi về bài học. Bạn có thể đặt câu hỏi công khai để nhận sự giúp đỡ từ cộng đồng, hoặc gửi câu hỏi riêng cho giáo viên.",
                "feature-inquiry",
                Arrays.asList("question-asking", "help-requests"));
                
        // Thêm dữ liệu về hỗ trợ kỹ thuật
        chatbotService.addTrainingData(
                "Tôi không nhận được mã xác thực email?",
                "Hãy kiểm tra trong thư mục Spam hoặc Quảng cáo. Nếu vẫn không thấy, bạn có thể nhấn nút 'Gửi lại mã xác thực' trong phần đăng ký hoặc đăng nhập.",
                "email-verification-issue",
                Arrays.asList("verify-email", "email-code-not-received"));

        // Thêm dữ liệu về tài khoản
        chatbotService.addTrainingData(
                "Làm sao để đổi mật khẩu?",
                "Bạn có thể đổi mật khẩu bằng cách: 1) Vào phần 'Cài đặt tài khoản', 2) Chọn 'Đổi mật khẩu', 3) Nhập mật khẩu cũ và mật khẩu mới, 4) Nhấn 'Lưu thay đổi'.",
                "account-change-password",
                Arrays.asList("change-password", "reset-password"));

        chatbotService.addTrainingData(
                "Tôi có thể xóa tài khoản không?",
                "Có, bạn có thể xóa tài khoản bằng cách gửi yêu cầu trong phần 'Cài đặt tài khoản' -> 'Xóa tài khoản'. Lưu ý: Sau khi xóa, toàn bộ dữ liệu sẽ bị mất vĩnh viễn.",
                "account-delete",
                Arrays.asList("delete-account", "remove-account"));

        // Thêm dữ liệu về mẹo học tập
        chatbotService.addTrainingData(
                "Làm sao để học hiệu quả hơn?",
                "Một số mẹo học tập hiệu quả: 1) Đặt mục tiêu nhỏ hàng ngày, 2) Dùng kỹ thuật Pomodoro, 3) Tự kiểm tra bằng quiz, 4) Tham gia nhóm học tập, 5) Sử dụng Learning Tree để theo dõi tiến độ.",
                "tips-effective-learning",
                Arrays.asList("learning-tips", "study-hacks"));

        chatbotService.addTrainingData(
                "Làm sao để ôn tập trước kỳ thi?",
                "Để ôn thi hiệu quả: 1) Tạo kế hoạch ôn tập bằng Schedule, 2) Làm quiz nhiều lần, 3) Xem lại tài liệu trong Document, 4) Hỏi đáp trong Inquiry, 5) Tập trung vào các mục tiêu Learning Goals.",
                "tips-exam-prep",
                Arrays.asList("exam-preparation", "study-for-tests"));

        // Thêm dữ liệu về phản hồi
        chatbotService.addTrainingData(
                "Tôi muốn góp ý cho ứng dụng thì gửi ở đâu?",
                "Bạn có thể gửi phản hồi trực tiếp qua mục 'Góp ý' trong phần Cài đặt, hoặc gửi email đến feedback@vibely.com.",
                "feedback-suggestions",
                Arrays.asList("app-feedback", "send-suggestions"));
        
        // Thêm dữ liệu cho người mới bắt đầu
        chatbotService.addTrainingData(
                "Tôi mới dùng Vibely, nên bắt đầu từ đâu?",
                "Bạn có thể bắt đầu bằng cách: 1) Cập nhật hồ sơ cá nhân, 2) Thêm các môn học (Subject), 3) Đặt mục tiêu đầu tiên trong Learning Goals, 4) Khám phá bài viết và nhóm học tập.",
                "onboarding-new-users",
                Arrays.asList("getting-started", "new-user-guide"));
        chatbotService.addTrainingData(
                "Làm sao để kết nối với bạn bè?",
                "Để kết nối với bạn bè: 1) Tìm kiếm tên người dùng, 2) Gửi yêu cầu kết bạn, 3) Sau khi chấp nhận, bạn có thể nhắn tin, mời vào nhóm học, hoặc cùng đặt mục tiêu chung.",
                "social-connect",
                Arrays.asList("add-friends", "make-connections"));
    }
}