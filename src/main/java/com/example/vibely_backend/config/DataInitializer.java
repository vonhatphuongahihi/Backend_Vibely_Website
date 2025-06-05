package com.example.vibely_backend.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.vibely_backend.service.ChatbotService;
import com.example.vibely_backend.service.ChatbotTrainingService;

@Component
public class DataInitializer implements CommandLineRunner {

        @Autowired
        private ChatbotService chatbotService;

        @Autowired
        private ChatbotTrainingService chatbotTrainingService;

        @Override
        public void run(String... args) {
                // addStaticTrainingData(); // MongoDB is down – skip for now
                // chatbotTrainingService.generateDynamicTrainingData(); // Nếu cũng ghi DB thì
                // comment luôn
        }

        private void addStaticTrainingData() {
                // Thêm dữ liệu mẫu về Vibely
                chatbotService.addTrainingData(
                                "Vibely là gì?",
                                "Vibely là một nền tảng học tập xã hội giúp người dùng theo dõi và chia sẻ quá trình học tập của mình. Bạn có thể đặt mục tiêu học tập, theo dõi tiến độ thông qua Learning Tree, và chia sẻ thành tích với bạn bè.",
                                "platform-intro",
                                Arrays.asList("platform-introduction", "what-is-vibely"));

                chatbotService.addTrainingData(
                                "Vibely có những tính năng gì?",
                                "Vibely cung cấp nhiều tính năng như: theo dõi tiến độ học tập qua Learning Tree, hệ thống thành tựu (Achievements), quản lý lịch học (Schedule), đặt và theo dõi mục tiêu học tập (Learning Goals), tạo và tham gia quiz, chia sẻ nội dung qua Posts và Story, lưu trữ tài liệu (Document), nhắn tin riêng tư (Message), và hỗ trợ đặt câu hỏi (Inquiry).",
                                "feature-vibely-features",
                                Arrays.asList("vibely-features", "usage-guide"));

                chatbotService.addTrainingData(
                                "Sứ mệnh của Vibely là gì?",
                                "Sứ mệnh của Vibely là tạo ra một môi trường học tập sáng tạo, nơi mọi người có thể cùng nhau trao đổi kiến thức và phát triển kỹ năng.",
                                "mission-vibely",
                                Arrays.asList("vibely-mission", "community"));

                chatbotService.addTrainingData(
                                "Vibely cam kết điều gì?",
                                "Vibely không chỉ là một nền tảng học tập mà còn là một mạng xã hội kết nối những người yêu thích giáo dục. Chúng tôi cam kết mang đến trải nghiệm học tập chất lượng, xây dựng cộng đồng gắn kết và thúc đẩy sự phát triển bền vững cho mọi thành viên.",
                                "vibely-commitments",
                                Arrays.asList("quality-learning", "engaged-community", "sustainable-development"));

                chatbotService.addTrainingData(
                                "Ai là người sáng lập dự án?",
                                "Dự án được sáng lập bởi một nhóm các nhà sáng lập tâm huyết gồm: Nhất Phương, Như Quỳnh, Phương Sang và Gia Minh.",
                                "project-founders",
                                Arrays.asList("founders", "team", "ceo"));

                chatbotService.addTrainingData(
                                "Thông tin về Nhất Phương là gì?",
                                "Nhất Phương là CEO & Founder của dự án, người có tầm nhìn chiến lược và định hướng phát triển toàn diện cho dự án.",
                                "founder-nhat-phuong",
                                Arrays.asList("nhat-phuong", "ceo", "founder"));

                chatbotService.addTrainingData(
                                "Cho tôi biết về Như Quỳnh?",
                                "Như Quỳnh là CEO & Founder, đóng vai trò chủ chốt trong việc phát triển ý tưởng và kết nối cộng đồng người dùng.",
                                "founder-nhu-quynh",
                                Arrays.asList("nhu-quynh", "ceo", "team"));

                chatbotService.addTrainingData(
                                "Phương Sang là ai?",
                                "Phương Sang là CEO & Founder, chuyên phụ trách chiến lược marketing và truyền thông cho dự án.",
                                "founder-phuong-sang",
                                Arrays.asList("phuong-sang", "marketing", "founder"));

                chatbotService.addTrainingData(
                                "Gia Minh là thành viên nào trong đội ngũ?",
                                "Gia Minh là CEO & Founder, người đóng góp vào phần phát triển công nghệ và trải nghiệm người dùng trong dự án.",
                                "founder-gia-minh",
                                Arrays.asList("gia-minh", "tech-lead", "founder"));

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

                // Tính năng đếm ngược ngày thi đại học
                chatbotService.addTrainingData(
                                "Tính năng đếm ngược ngày thi đại học có tác dụng gì?",
                                "Tính năng đếm ngược ngày thi đại học sẽ giúp bạn theo dõi thời gian còn lại trước kỳ thi đại học. Bạn có thể biết chính xác số ngày còn lại để chuẩn bị tốt nhất cho kỳ thi quan trọng này.",
                                "countdown-to-college-exam",
                                Arrays.asList("exam-countdown", "college-entrance-exam"));

                // Chức năng thời tiết
                chatbotService.addTrainingData(
                                "Chức năng thời tiết có tác dụng gì?",
                                "Chức năng thời tiết sẽ cung cấp cho bạn thông tin về dự báo thời tiết ở khu vực của bạn. Bạn có thể xem nhiệt độ, tình trạng trời (nắng, mưa, gió, v.v.). Điều này sẽ giúp bạn lên kế hoạch và chuẩn bị tốt hơn cho các hoạt động hàng ngày.",
                                "weather-forecast",
                                Arrays.asList("weather-information", "weather-alerts"));

                // Tính năng "Đã lưu" - dùng để lưu tài liệu
                chatbotService.addTrainingData(
                                "Tính năng 'Đã lưu' dùng để làm gì?",
                                "Tính năng 'Đã lưu' cho phép bạn lưu lại những tài liệu hoặc nội dung học tập quan trọng để xem lại sau. Bạn có thể truy cập nhanh mục 'Tài liệu' từ hồ sơ cá nhân.",
                                "saved-feature",
                                Arrays.asList("save-post", "save-document", "luu-tai-lieu"));

                // Thêm dữ liệu về cách sử dụng
                chatbotService.addTrainingData(
                                "Làm sao để đặt mục tiêu học tập?",
                                "Để đặt mục tiêu học tập, bạn cần: 1) Vào phần Learning Goals trong Cây học tập, 2) Nhấn nút 'Thêm mục tiêu', 3) Điền thông tin mục tiêu như tiêu đề, mô tả, thời hạn, 4) Nhấn 'Lưu' để hoàn tất.",
                                "how-to-learning-goals",
                                Arrays.asList("set-learning-goals", "create-goals"));

                chatbotService.addTrainingData(
                                "Làm sao để tạo bài viết?",
                                "Để tạo bài viết: 1) Vào phần Posts, 2) Nhấn nút 'Tạo bài viết', 3) Viết nội dung bài viết, 4) Thêm hình ảnh nếu muốn, 5) Nhấn 'Đăng' để chia sẻ với mọi người.",
                                "how-to-posts",
                                Arrays.asList("create-posts", "share-posts"));

                chatbotService.addTrainingData(
                                "Làm sao để học hiệu quả hơn trên Vibely?",
                                "Bạn có thể học hiệu quả hơn bằng cách: 1) Đặt mục tiêu nhỏ mỗi ngày qua Learning Goals trong chức năng Cây học tập, 2) Sử dụng tính năng Schedule để nhắc nhở học tập, 3) Chia sẻ tiến trình để có động lực từ bạn bè.",
                                "tips-effective-learning",
                                Arrays.asList("learning-tips", "productivity"));

                // Thêm dữ liệu về Story
                chatbotService.addTrainingData(
                                "Story là gì?",
                                "Story là tính năng cho phép bạn chia sẻ khoảnh khắc học tập của mình dưới dạng hình ảnh hoặc video. Story sẽ tự động biến mất sau 24 giờ, giúp bạn chia sẻ nhanh chóng và tự nhiên hơn.",
                                "feature-story",
                                Arrays.asList("story-feature", "moment-sharing"));

                chatbotService.addTrainingData(
                                "Làm sao để tạo và chia sẻ Story?",
                                "Để tạo và chia sẻ Story: 1) Tại trang chủ, nhấn vào 'Tạo tin', 2) Chọn ảnh, 3) Nhấn 'Đăng' để chia sẻ với mọi người.",
                                "how-to-create-story",
                                Arrays.asList("create-story", "share-story"));

                chatbotService.addTrainingData(
                                "Story khác với Posts như thế nào?",
                                "Khác với Posts, Story là hình thức chia sẻ nhanh chóng và tự nhiên hơn. Story sẽ tự động biến mất sau 24 giờ, trong khi Posts được lưu trữ lâu dài và có thể tương tác qua comments.",
                                "story-vs-posts",
                                Arrays.asList("story-feature", "posts-feature"));

                chatbotService.addTrainingData(
                                "Tôi có thể xem lại Story đã chia sẻ không?",
                                "Không, Story sẽ tự động biến mất sau 24 giờ. Nếu bạn muốn lưu lại các khoảnh khắc, bạn nên chia sẻ dưới dạng Posts thay vì Story.",
                                "view-past-story",
                                Arrays.asList("story-expiration", "save-story-content"));

                // Thêm dữ liệu về cách thao tác với Story
                chatbotService.addTrainingData(
                                "Tôi có thể tương tác với Story của người khác như thế nào?",
                                "Với các Story của người khác, hiện tại bạn có thể thực hiện các biểu cảm 'tim' để tương tác.",
                                "interact-with-story",
                                Arrays.asList("like-story", "comment-on-story", "share-story"));

                chatbotService.addTrainingData(
                                "Tôi có thể chỉnh sửa hoặc xóa Story của mình không?",
                                "Không, bạn không thể chỉnh sửa các Story đã chia sẻ, chỉ xóa được. Story sẽ tự động biến mất sau 24 giờ. Nếu muốn thay đổi nội dung, bạn phải chia sẻ một Story mới.",
                                "edit-delete-story",
                                Arrays.asList("story-expiration", "update-story-content"));

                // Thêm dữ liệu về Posts
                chatbotService.addTrainingData(
                                "Posts có tác dụng gì?",
                                "Posts cho phép bạn chia sẻ bài viết dài về quá trình học tập, kinh nghiệm, hoặc tài liệu học tập. Khác với Story, Posts sẽ được lưu trữ lâu dài và có thể tương tác qua comments.",
                                "feature-posts",
                                Arrays.asList("posts-feature", "long-form-content"));

                chatbotService.addTrainingData(
                                "Làm sao để tạo bài viết (Posts)?",
                                "Để tạo bài viết: 1) Vào phần Posts, 2) Nhấn vào sẽ hiện 'Tạo bài viết', 3) Viết nội dung bài viết, 4) Thêm hình ảnh, video, biểu cảm nếu muốn, 5) Nhấn 'Đăng' để chia sẻ với mọi người.",
                                "how-to-create-posts",
                                Arrays.asList("create-posts", "share-posts"));

                chatbotService.addTrainingData(
                                "Tôi có thể tương tác với bài viết của người khác như thế nào?",
                                "Với các bài viết của người khác, bạn có thể: 1) Để lại bình luận, 2) Thể hiện biểu cảm, 3) Chia sẻ bài viết đó lên trang cá nhân của bạn.",
                                "interact-with-posts",
                                Arrays.asList("comment-on-posts", "like-posts", "share-posts"));

                chatbotService.addTrainingData(
                                "Tôi có thể lưu trữ tài liệu học tập trong Posts không?",
                                "Có, bạn có thể chia sẻ các tài liệu học tập dạng PDF, Word, Excel trong các bài viết Posts bằng cách đính kèm đường dẫn (link) đến các tệp tin đó. Tuy nhiên, bạn không thể trực tiếp upload và lưu trữ các tệp tin này trong Posts, chỉ hỗ trợ dạng ảnh/ video và văn bản.",
                                "posts-document-sharing",
                                Arrays.asList("share-learning-materials", "document-in-posts"));

                // Thêm dữ liệu về sửa, xóa bài viết
                chatbotService.addTrainingData(
                                "Tôi có thể chỉnh sửa bài viết đã đăng không?",
                                "Có, bạn có thể chỉnh sửa bài viết đã đăng bằng cách nhấn vào biểu tượng 3 chấm, chọn 'Sửa bài viết' ở góc bài viết. Sau đó, bạn có thể thay đổi nội dung, hình ảnh hoặc liên kết trong bài viết.",
                                "edit-post",
                                Arrays.asList("chinh-sua-post", "edit-post", "update-post"));

                chatbotService.addTrainingData(
                                "Làm thế nào để sửa một bài viết?",
                                "Để sửa bài viết, bạn truy cập vào bài viết của mình, nhấn vào biểu tượng ba chấm và chọn 'Sửa bài viết'. Sau khi chỉnh sửa xong, nhấn 'Hoàn tất' để cập nhật thay đổi.",
                                "how-to-edit-post",
                                Arrays.asList("edit", "chinh-sua", "cap-nhat-post"));

                chatbotService.addTrainingData(
                                "Tôi có thể xóa bài viết không?",
                                "Có, bạn có thể xóa bài viết của mình bất cứ lúc nào. Nhấn vào biểu tượng ba chấm ở góc bài viết và chọn 'Xóa bài viết'. Hệ thống sẽ yêu cầu xác nhận trước khi xóa.",
                                "delete-post",
                                Arrays.asList("xoa-post", "delete", "remove-post"));

                chatbotService.addTrainingData(
                                "Làm sao để xóa một bài viết đã đăng?",
                                "Bạn có thể xóa bài viết đã đăng bằng cách nhấn vào biểu tượng tùy chọn (ba chấm) trên bài viết, sau đó chọn 'Xóa bài viết'. Bài viết sẽ bị xóa vĩnh viễn và không thể khôi phục.",
                                "how-to-delete-post",
                                Arrays.asList("delete-post", "xoa-bai", "remove-post"));

                // Tính năng phát video trong bài viết (Post)
                chatbotService.addTrainingData(
                                "Khi đăng bài viết có video, tôi có thể làm gì với video đó?",
                                "Khi bạn đăng bài viết có kèm video, bạn có thể phát video, điều chỉnh âm lượng, chuyển sang chế độ toàn màn hình, và sử dụng menu dấu ba chấm để thực hiện các thao tác như tải xuống, thay đổi tốc độ phát, hoặc bật chế độ hình trong hình.",
                                "post-video-features",
                                Arrays.asList("video-in-post", "video-controls", "play-video"));

                // Thêm dữ liệu về Learning Goals
                chatbotService.addTrainingData(
                                "Learning Goals là gì?",
                                "Learning Goals là tính năng giúp bạn đặt và theo dõi các mục tiêu học tập. Bạn có thể đặt mục tiêu ngắn hạn hoặc dài hạn, theo dõi tiến độ, và nhận thông báo khi hoàn thành.",
                                "feature-learning-goals",
                                Arrays.asList("learning-goals-feature", "goal-tracking"));

                // Thêm dữ liệu về Quiz
                chatbotService.addTrainingData(
                                "Quiz là gì?",
                                "Quiz là tính năng cho phép bạn tạo và tham gia các bài kiểm tra kiến thức. Sau khi tham gia một bài quiz bất kỳ bạn có thể xem kết quả ngay lập tức.",
                                "feature-quiz",
                                Arrays.asList("quiz-feature", "knowledge-test"));

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
                                "Để bảo vệ thông tin cá nhân: 1) Không chia sẻ mật khẩu, 2) Chỉ kết bạn với người quen.",
                                "privacy-protection",
                                Arrays.asList("personal-data-protection", "account-security"));

                // Thêm dữ liệu về Trung tâm trợ giúp
                chatbotService.addTrainingData(
                                "Trung tâm trợ giúp là gì?",
                                "Vibely cung cấp Trung tâm trợ giúp với các tính năng như: hướng dẫn sử dụng (cách tạo tài khoản và kết bạn) và cách quản lý tài khoản (đăng nhập và mật khẩu).",
                                "help-center",
                                Arrays.asList("support", "faq", "troubleshoot", "feedback"));

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
                                "Hãy kiểm tra trong thư mục Spam hoặc Quảng cáo. Nếu vẫn không thấy, bạn có thể nhấn nút 'Gửi lại mã xác thực' trong phần đăng ký hoặc đăng nhập khi nhập mã xác thực.",
                                "email-verification-issue",
                                Arrays.asList("verify-email", "email-code-not-received"));

                // Thêm dữ liệu quy trình đổi mật khẩu
                chatbotService.addTrainingData(
                                "Làm sao để đổi mật khẩu?",
                                "Bạn có thể đổi mật khẩu bằng cách: 1) Vào phần 'Cài đặt', 2) Chọn 'Đổi mật khẩu', 3) Nhập mật khẩu cũ, mật khẩu mới và nhập lại mật khẩu mới, 4) Nhấn 'Đặt lại mật khẩu', 5) Thông báo 'Đặt lại mật khẩu thành công'.",
                                "account-change-password",
                                Arrays.asList("change-password", "reset-password"));

                // Thêm dữ liệu về cách tạo tài khoản
                chatbotService.addTrainingData(
                                "Làm sao để tạo một tài khoản mới?",
                                "Để tạo tài khoản mới: 1) Tại trang Đăng ký, nhập thông tin cá nhân theo hướng dẫn, 2) Nhấn vào 'Đăng ký', 3) Nhập mã xác thực được gửi qua email để hoàn tất.",
                                "create-account",
                                Arrays.asList("sign-up", "register-new-user"));

                // Thêm dữ liệu về đăng nhập
                chatbotService.addTrainingData(
                                "Tôi có thể đăng nhập bằng cách nào?",
                                "Bạn có thể đăng nhập bằng email và mật khẩu đã đăng ký. Ngoài ra, hệ thống còn hỗ trợ đăng nhập bằng Google, Github hoặc Facebook.",
                                "how-to-login",
                                Arrays.asList("login", "sign-in", "authentication"));

                chatbotService.addTrainingData(
                                "Tôi có thể sử dụng tài khoản Google để đăng nhập không?",
                                "Có, hệ thống hỗ trợ đăng nhập bằng tài khoản Google. Bạn chỉ cần chọn 'Google' khi vào trang đăng nhập.",
                                "login-with-google",
                                Arrays.asList("google-login", "social-login", "dang-nhap-google"));

                chatbotService.addTrainingData(
                                "Có thể đăng nhập bằng Github không?",
                                "Có, hệ thống hỗ trợ, bạn có thể đăng nhập bằng Github. Nhấn 'Github' và làm theo hướng dẫn.",
                                "login-with-facebook",
                                Arrays.asList("facebook-login", "social-auth", "dang-nhap-github"));

                // Thêm dữ liệu về hướng dẫn xóa tài khoản
                chatbotService.addTrainingData(
                                "Tôi có thể xóa tài khoản không?",
                                "Có, bạn có thể xóa tài khoản bằng cách gửi yêu cầu trong phần 'Cài đặt' -> 'Xóa tài khoản' -> 'Xác nhận'. Lưu ý: Sau khi xóa, toàn bộ dữ liệu sẽ bị mất vĩnh viễn.",
                                "account-delete",
                                Arrays.asList("delete-account", "remove-account"));

                // Thêm dữ liệu về quy trình quên mật khẩu
                chatbotService.addTrainingData(
                                "Nếu quên mật khẩu thì sao?",
                                "Để khôi phục mật khẩu, bạn cần nhấn vào 'Quên mật khẩu?' tại trang 'Đăng nhập', nhập địa chỉ email, nhập mã xác thực được gửi trong email, tiếp theo tiến hành nhập mật khẩu mới và nhập lại mật khẩu mới để xác nhận. Để đăng nhập, vui lòng nhập theo mật khẩu vừa tạo.",
                                "password-recovery",
                                Arrays.asList("forgot-password", "reset-password"));

                // Thêm dữ liệu về mẹo học tập
                chatbotService.addTrainingData(
                                "Làm sao để học hiệu quả hơn?",
                                "Một số mẹo học tập hiệu quả: 1) Đặt mục tiêu nhỏ hàng ngày qua Learning Goals trong chức năng Cây học tập, 2) Dùng kỹ thuật Pomodoro, 3) Tự kiểm tra bằng quiz, 4) Sử dụng Learning Tree để theo dõi tiến độ.",
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
                                "Bạn có thể gửi phản hồi trực tiếp qua mục 'Hộp thư hỗ trợ', hoặc gửi email đến Vibely(22520861@gm.uit.edu.vn).",
                                "feedback-suggestions",
                                Arrays.asList("app-feedback", "send-suggestions"));

                // Thêm dữ liệu cho người mới bắt đầu
                chatbotService.addTrainingData(
                                "Tôi mới dùng Vibely, nên bắt đầu từ đâu?",
                                "Bạn có thể bắt đầu bằng cách: 1) Cập nhật hồ sơ cá nhân, 2) Khám phá chức năng Cây học tập, 3) Khám phá chế độ Pomodoro, 4) Khám phá tài liệu và củng cố kiến thức, 5) Khám phá chức năng lịch.",
                                "onboarding-new-users",
                                Arrays.asList("getting-started", "new-user-guide"));

                chatbotService.addTrainingData(
                                "Làm sao để kết nối với bạn bè?",
                                "Để kết nối với bạn bè: 1) Tìm kiếm tên người dùng, 2) Gửi yêu cầu kết bạn, 3) Sau khi chấp nhận, bạn có thể nhắn tin, chia sẻ thông tin, bài viết, ...",
                                "social-connect",
                                Arrays.asList("add-friends", "make-connections"));

                // Thêm dữ liệu về chế độ pomodoro
                chatbotService.addTrainingData(
                                "Chế độ Pomodoro dùng cho mục đích gì?",
                                "Chế độ Pomodoro giúp bạn tăng cường sự tập trung và hiệu suất làm việc bằng cách chia nhỏ thời gian học tập thành các khoảng thời gian 25 phút (Pomodoro), nghỉ ngắn (5 phút) và nghỉ dài (15 phút). Sau mỗi 4 Pomodoro, bạn có thể nghỉ dài hơn (15-30 phút).",
                                "feature-pomodoro",
                                Arrays.asList("pomodoro-technique", "time-management"));

                // Thêm dữ liệu về Cây học tập
                chatbotService.addTrainingData(
                                "Cây học tập là gì?",
                                "Cây học tập là một công cụ giúp bạn theo dõi tiến độ học tập và đặt mục tiêu cần hoàn thành. Khi bạn hoàn thành các mục tiêu học tập, cây của bạn sẽ phát triển, thể hiện sự tiến bộ của bạn.",
                                "feature-learning-tree",
                                Arrays.asList("learning-tree", "study-progress"));

                chatbotService.addTrainingData(
                                "Cây học tập có ích gì?",
                                "Cây học tập khuyến khích sự kiên trì và tạo động lực cho bạn trong quá trình học. Nó giúp bạn tổ chức và quản lý các mục tiêu học tập một cách hiệu quả.",
                                "feature-learning-tree-benefits",
                                Arrays.asList("learning-tree-benefits", "motivation"));

                chatbotService.addTrainingData(
                                "Quá trình học tập đạt thành tựu có mấy cấp độ?",
                                "Trong Vibely, quá trình học tập đạt thành tựu được chia thành 6 cấp độ: Tân Binh, Tập Sự, Chiến Binh, Tinh Anh, Cao Thủ và Thần Vương.",
                                "feature-achievement-levels",
                                Arrays.asList("achievement-levels", "learning-progress"));

                // Thêm dữ liệu về cách đạt từng thành tựu trong cây học tập
                chatbotService.addTrainingData(
                                "Làm sao để đạt thành tựu Tân Binh?",
                                "Để đạt thành tựu Tân Binh, bạn cần hoàn thành 1 mục tiêu học tập cơ bản. Bạn có thể bắt đầu bằng cách chọn một chủ đề đơn giản, như đọc một chương sách hoặc hoàn thành một bài tập ngắn. Sau khi hoàn thành, hãy ghi nhận thành tích của mình để có động lực tiếp tục.",
                                "feature-achievement-novice-goal",
                                Arrays.asList("achievement-novice-goal", "learning-tree"));

                chatbotService.addTrainingData(
                                "Làm sao để đạt thành tựu Tân Binh?",
                                "Để đạt thành tựu Tân Binh, bạn cần hoàn thành từ 5 mục tiêu.",
                                "feature-achievement-novice",
                                Arrays.asList("achievement-novice", "learning-tree"));

                chatbotService.addTrainingData(
                                "Làm sao để đạt thành tựu Chiến Binh?",
                                "Để đạt thành tựu Chiến Binh, bạn cần hoàn thành từ 10 mục tiêu.",
                                "feature-achievement-warrior",
                                Arrays.asList("achievement-warrior", "learning-tree"));

                chatbotService.addTrainingData(
                                "Làm sao để đạt thành tựu Tinh Anh?",
                                "Để đạt thành tựu Tinh Anh, bạn cần hoàn thành từ 20 mục tiêu",
                                "feature-achievement-elite",
                                Arrays.asList("achievement-elite", "learning-tree"));

                chatbotService.addTrainingData(
                                "Làm sao để đạt thành tựu Cao Thủ?",
                                "Để đạt thành tựu Cao Thủ, bạn cần hoàn thành từ 50 mục tiêu.",
                                "feature-achievement-master",
                                Arrays.asList("achievement-master", "learning-tree"));

                chatbotService.addTrainingData(
                                "Làm sao để đạt thành tựu Thần Vương?",
                                "Để đạt thành tựu Thần Vương, bạn cần hoàn thành từ 100 mục tiêu.",
                                "feature-achievement-king",
                                Arrays.asList("achievement-king", "learning-tree"));

                // Thêm dữ liệu về cách dùng/hướng dẫn chức năng cây học tập
                chatbotService.addTrainingData(
                                "Làm sao để sử dụng cây học tập?",
                                "Để sử dụng cây học tập, truy cập tab 'Cây học tập', nhấp 'Bắt đầu hành trình', chọn cây, thêm các mục tiêu cần làm, sau đó với mỗi mục tiêu hoàn thành, nhấp chọn để cập nhật tiến độ.",
                                "feature-learning-tree-usage",
                                Arrays.asList("learning-tree-usage", "usage-guide"));

                chatbotService.addTrainingData(
                                "Cây học tập có những tính năng gì?",
                                "Cây học tập bao gồm: theo dõi tiến trình, đặt mục tiêu, nhận thưởng, chia sẻ kết quả.",
                                "feature-learning-tree-features",
                                Arrays.asList("learning-tree-features", "usage-guide"));
        }
}