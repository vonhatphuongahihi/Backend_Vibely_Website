package com.example.vibely_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.vibely_backend.dto.request.UserProfileUpdateRequest;
import com.example.vibely_backend.dto.response.ApiResponse;
import com.example.vibely_backend.dto.response.BioResponse;
import com.example.vibely_backend.dto.response.DocumentResponse;
import com.example.vibely_backend.dto.response.MutualFriendResponse;
import com.example.vibely_backend.dto.response.SimpleUserResponse;
import com.example.vibely_backend.dto.response.UserInfoResponse;
import com.example.vibely_backend.dto.response.UserProfileResponse;
import com.example.vibely_backend.entity.Bio;
import com.example.vibely_backend.entity.Conversation;
import com.example.vibely_backend.entity.DocumentUser;
import com.example.vibely_backend.entity.Level;
import com.example.vibely_backend.entity.Post;
import com.example.vibely_backend.entity.Provider;
import com.example.vibely_backend.entity.Story;
import com.example.vibely_backend.entity.Subject;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.BioRepository;
import com.example.vibely_backend.repository.ConversationRepository;
import com.example.vibely_backend.repository.DocumentRepository;
import com.example.vibely_backend.repository.InquiryRepository;
import com.example.vibely_backend.repository.LevelRepository;
import com.example.vibely_backend.repository.MessageRepository;
import com.example.vibely_backend.repository.PostRepository;
import com.example.vibely_backend.repository.StoryRepository;
import com.example.vibely_backend.repository.SubjectRepository;
import com.example.vibely_backend.repository.UserRepository;
import com.example.vibely_backend.service.oauth2.OAuth2UserDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BioRepository bioRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    private EmailService emailService;

    // Lưu trữ OTP tạm thời
    private Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public void sendOtpToEmail(String email) {
        // Tạo mã OTP ngẫu nhiên 6 chữ số
        String otp = String.format("%06d", new Random().nextInt(1000000));

        // Lưu OTP tạm thời
        otpStorage.put(email, otp);

        // Gửi OTP qua email
        emailService.sendRegisterOtpCode(email, otp);
    }

    public boolean verifyOtp(String email, String otp) {
        // Kiểm tra xem email có tồn tại trong otpStorage không
        if (!otpStorage.containsKey(email)) {
            log.warn("OTP không tồn tại cho email: " + email);
            return false; // Không tìm thấy OTP
        }

        // Lấy OTP đã lưu và so sánh
        String storedOtp = otpStorage.get(email);
        if (storedOtp.equals(otp)) {
            // Xóa OTP sau khi xác thực thành công
            otpStorage.remove(email);
            log.info("Xác thực OTP thành công cho email: " + email);
            return true; // Xác thực thành công
        } else {
            log.warn("OTP không hợp lệ cho email: " + email);
            return false; // OTP không hợp lệ
        }
    }

    public User register(User user) {

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Tên tài khoản là bắt buộc");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Mật khẩu là bắt buộc");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email là bắt buộc");
        }

        // Chỉ kiểm tra email trùng
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Email đã tồn tại: {}", user.getEmail());
            throw new RuntimeException("Email đã tồn tại");
        }

        // Thiết lập
        user.setFollowers(new ArrayList<>());
        user.setFollowings(new ArrayList<>());
        user.setPosts(new ArrayList<>());
        user.setLikedPosts(new ArrayList<>());
        user.setSavedPosts(new ArrayList<>());
        user.setSavedDocuments(new ArrayList<>());
        user.setProfilePicture("https://res.cloudinary.com/dxdqjj2ww/image/upload/v1747751488/default_weq0sm.png");
        user.setCoverPicture("");
        user.setPostsCount(0);
        user.setFollowerCount(0);
        user.setFollowingCount(0);
        user.setPassword(encoder.encode(user.getPassword()));

        try {
            User savedUser = userRepository.save(user);
            return savedUser;
        } catch (Exception e) {
            log.error("Lỗi đăng ký tài khoản: {}", e.getMessage());
            throw new RuntimeException("Lỗi đăng ký tài khoản: " + e.getMessage());
        }
    }

    public String verify(User user) {
        String usernameOrEmail = user.getUsername() != null ? user.getUsername() : user.getEmail();

        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            log.warn("Username hoặc email trống");
            throw new RuntimeException("Username hoặc email là bắt buộc");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            log.warn("Mật khẩu trống");
            throw new RuntimeException("Mật khẩu là bắt buộc");
        }

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, user.getPassword()));

            if (authentication.isAuthenticated()) {
                // Lấy user từ database để có đầy đủ thông tin
                User authenticatedUser = userRepository.findByEmail(usernameOrEmail)
                        .orElseGet(() -> userRepository.findByUsername(usernameOrEmail)
                                .orElseThrow(() -> new RuntimeException("User not found")));

                // Tạo token với userId và email
                return jwtService.generateToken(authenticatedUser.getId(), authenticatedUser.getEmail());
            } else {
                log.warn("Xác thực thất bại cho người dùng: {}", usernameOrEmail);
                throw new RuntimeException("Xác thực thất bại");
            }
        } catch (AuthenticationException e) {
            log.warn("Lỗi xác thực: {}", e.getMessage());
            throw new RuntimeException("Username hoặc password không đúng");
        } catch (Exception e) {
            log.error("Lỗi không xác định trong quá trình xác thực: {}", e.getMessage());
            throw new RuntimeException("Lỗi xác thực: " + e.getMessage());
        }
    }

    public String generateToken(User user) {
        return jwtService.generateToken(user.getEmail());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateUserProfile(String userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        return userRepository.save(user);
    }

    public UserInfoResponse convertToUserInfoResponse(User user) {
        UserInfoResponse response = new UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setGender(user.getGender());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setProfilePicture(user.getProfilePicture());
        response.setCoverPicture(user.getCoverPicture());
        response.setFollowers(user.getFollowers());
        response.setFollowings(user.getFollowings());
        response.setPosts(user.getPosts());
        response.setLikedPosts(user.getLikedPosts());
        response.setSavedPosts(user.getSavedPosts());
        response.setSavedDocuments(user.getSavedDocuments());
        response.setPostsCount(user.getPostsCount());
        response.setFollowerCount(user.getFollowerCount());
        response.setFollowingCount(user.getFollowingCount());

        // Get bio from bioRepository using userId
        Bio bio = bioRepository.findByUserId(user.getId()).orElse(null);
        if (bio != null) {
            BioResponse bioResponse = new BioResponse(
                    bio.getBioText(),
                    bio.getLiveIn(),
                    bio.getRelationship(),
                    bio.getWorkplace(),
                    bio.getEducation(),
                    bio.getHometown());
            response.setBio(bioResponse);
        }

        return response;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return jwtService.extractUserIdFromToken(auth.getCredentials().toString());
    }

    public ApiResponse followUser(String userIdToFollow) {
        String currentUserId = getCurrentUserId();

        if (currentUserId.equals(userIdToFollow)) {
            return new ApiResponse("error", "Bạn không được phép theo dõi chính mình", null);
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElse(null);
        if (currentUser == null) {
            return new ApiResponse("error", "Người dùng hiện tại không tồn tại", null);
        }

        User userToFollow = userRepository.findById(userIdToFollow)
                .orElse(null);
        if (userToFollow == null) {
            return new ApiResponse("error", "Người dùng cần theo dõi không tồn tại", null);
        }

        if (currentUser.getFollowings().contains(userIdToFollow)) {
            return new ApiResponse("error", "Bạn đã theo dõi người dùng này", null);
        }

        // Thêm ID vào danh sách
        currentUser.getFollowings().add(userIdToFollow);
        userToFollow.getFollowers().add(currentUserId);

        currentUser.setFollowingCount(currentUser.getFollowings().size());
        userToFollow.setFollowerCount(userToFollow.getFollowers().size());

        userRepository.save(currentUser);
        userRepository.save(userToFollow);

        return new ApiResponse("success", "Theo dõi người dùng thành công", null);
    }

    public ApiResponse unfollowUser(String userIdToUnfollow) {
        String currentUserId = getCurrentUserId();

        if (currentUserId.equals(userIdToUnfollow)) {
            return new ApiResponse("error", "Bạn không được phép bỏ theo dõi chính mình", null);
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElse(null);
        if (currentUser == null) {
            return new ApiResponse("error", "Người dùng hiện tại không tồn tại", null);
        }

        User userToUnfollow = userRepository.findById(userIdToUnfollow)
                .orElse(null);
        if (userToUnfollow == null) {
            return new ApiResponse("error", "Người dùng cần bỏ theo dõi không tồn tại", null);
        }

        boolean isFollowing = currentUser.getFollowings().contains(userIdToUnfollow);

        if (!isFollowing) {
            return new ApiResponse("error", "Bạn chưa theo dõi người dùng này", null);
        }

        // Xóa ID khỏi danh sách
        currentUser.getFollowers().remove(userIdToUnfollow);
        currentUser.getFollowings().remove(userIdToUnfollow);
        userToUnfollow.getFollowers().remove(currentUserId);
        userToUnfollow.getFollowings().remove(currentUserId);

        currentUser.setFollowerCount(currentUser.getFollowers().size());
        currentUser.setFollowingCount(currentUser.getFollowings().size());
        userToUnfollow.setFollowerCount(userToUnfollow.getFollowers().size());
        userToUnfollow.setFollowingCount(userToUnfollow.getFollowings().size());

        userRepository.save(currentUser);
        userRepository.save(userToUnfollow);

        return new ApiResponse("success", "Bỏ theo dõi người dùng thành công", null);
    }

    public ApiResponse deleteFriendRequest(String requestSenderId) {
        String currentUserId = getCurrentUserId();

        User currentUser = userRepository.findById(currentUserId)
                .orElse(null);
        if (currentUser == null) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        User sender = userRepository.findById(requestSenderId)
                .orElse(null);
        if (sender == null) {
            return new ApiResponse("error", "Người gửi yêu cầu không tồn tại", null);
        }

        if (!sender.getFollowings().contains(currentUserId)) {
            return new ApiResponse("error", "Không tìm thấy yêu cầu kết bạn", null);
        }

        sender.getFollowings().remove(currentUserId);
        currentUser.getFollowers().remove(requestSenderId);

        currentUser.setFollowerCount(currentUser.getFollowers().size());
        sender.setFollowingCount(sender.getFollowings().size());

        userRepository.save(currentUser);
        userRepository.save(sender);

        return new ApiResponse("success", "Đã xóa lời mời kết bạn từ " + sender.getUsername(), null);
    }

    public ApiResponse getAllFriendRequests() {
        String currentUserId = getCurrentUserId();

        User currentUser = userRepository.findById(currentUserId)
                .orElse(null);
        if (currentUser == null) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        // Lấy danh sách ID người gửi lời mời kết bạn
        List<String> requestSenderIds = currentUser.getFollowers().stream()
                .filter(followerId -> !currentUser.getFollowings().contains(followerId))
                .toList();

        // Lấy thông tin người dùng từ danh sách ID
        List<SimpleUserResponse> requests = userRepository.findAllById(requestSenderIds)
                .stream()
                .map(user -> new SimpleUserResponse(user.getId(), user.getUsername(), user.getProfilePicture()))
                .toList();

        return new ApiResponse("success", "Lấy tất cả lời mời kết bạn thành công", requests);
    }

    public ApiResponse getAllUserForRequest() {
        String currentUserId = getCurrentUserId();

        User currentUser = userRepository.findById(currentUserId)
                .orElse(null);
        if (currentUser == null) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        Set<String> excludedIds = new HashSet<>();
        excludedIds.add(currentUser.getId());
        excludedIds.addAll(currentUser.getFollowers());
        excludedIds.addAll(currentUser.getFollowings());

        // Lấy tất cả người dùng và chuyển đổi sang SimpleUserResponse
        List<SimpleUserResponse> allUsers = userRepository.findAll().stream()
                .map(user -> new SimpleUserResponse(user.getId(), user.getUsername(), user.getProfilePicture()))
                .toList();

        // Lọc ra những người chưa liên quan
        List<SimpleUserResponse> suggestions = allUsers.stream()
                .filter(user -> !excludedIds.contains(user.getId()))
                .toList();

        return new ApiResponse("success", "Lấy tất cả đề xuất kết bạn thành công", suggestions);
    }

    public ApiResponse getMutualFriends(String profileUserId) {
        User profileUser = userRepository.findById(profileUserId)
                .orElse(null);
        if (profileUser == null) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        // Lấy danh sách ID mà profileUser đang follow
        Set<String> followingIds = new HashSet<>(profileUser.getFollowings());

        // Lọc follower nào cũng đang được follow lại (bạn chung)
        List<String> mutualFriendIds = profileUser.getFollowers().stream()
                .filter(followerId -> followingIds.contains(followerId))
                .toList();

        // Lấy thông tin chi tiết của bạn chung
        List<MutualFriendResponse> mutualFriends = userRepository.findAllById(mutualFriendIds)
                .stream()
                .map(user -> new MutualFriendResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getProfilePicture(),
                        user.getEmail(),
                        user.getFollowers().size(),
                        user.getFollowings().size()))
                .toList();

        return new ApiResponse("success", "Lấy danh sách bạn chung thành công", mutualFriends);
    }

    public ApiResponse getAllUsers() {
        List<MutualFriendResponse> userInfos = userRepository.findAll().stream()
                .map(user -> new MutualFriendResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getProfilePicture(),
                        user.getEmail(),
                        user.getFollowers().size(),
                        user.getFollowings().size()))
                .toList();

        return new ApiResponse("success", "Lấy tất cả người dùng thành công", userInfos);
    }

    public ApiResponse getUserProfile(String userId) {
        String loggedInUserId = getCurrentUserId();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        User user = optionalUser.get();
        Bio bio = bioRepository.findByUserId(userId).orElse(null);

        BioResponse bioResponse = bio != null ? new BioResponse(
                bio.getBioText(),
                bio.getLiveIn(),
                bio.getRelationship(),
                bio.getWorkplace(),
                bio.getEducation(),
                bio.getHometown()) : null;

        UserProfileResponse userProfile = new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getGender(),
                user.getDateOfBirth(),
                user.getProfilePicture(),
                user.getCoverPicture(),
                bioResponse);

        boolean isOwner = userId.equals(loggedInUserId);

        Map<String, Object> result = new HashMap<>();
        result.put("profile", userProfile);
        result.put("isOwner", isOwner);

        return new ApiResponse("success", "Lấy hồ sơ người dùng thành công", result);
    }

    public ApiResponse getUsersByIds(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ApiResponse("error", "Danh sách userId trống!", null);
        }

        List<User> users = userRepository.findAllById(userIds);

        List<SimpleUserResponse> responseList = users.stream().map(user -> new SimpleUserResponse(
                user.getId(),
                user.getUsername(),
                user.getProfilePicture())).toList();

        return new ApiResponse("success", "Lấy danh sách người dùng thành công", responseList);
    }

    public ApiResponse getSavedDocuments(String query, String level, String subject) {
        String userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        // Lấy danh sách tài liệu đã lưu từ repository
        List<DocumentUser> savedDocs = documentRepository.findAllById(user.getSavedDocuments());

        // Lọc theo điều kiện
        List<DocumentUser> filteredDocs = savedDocs.stream()
                .filter(doc -> (query == null || query.isBlank()
                        || doc.getTitle().toLowerCase().contains(query.toLowerCase())) &&
                        (level == null || level.isBlank() || doc.getLevelId().equals(level)) &&
                        (subject == null || subject.isBlank() || doc.getSubjectId().equals(subject)))
                .toList();

        List<DocumentResponse> responses = filteredDocs.stream().map(doc -> {
            Level levelRes = null;
            Subject subjectRes = null;
            if (doc.getLevelId() != null) {
                levelRes = levelRepository.findById(doc.getLevelId()).orElse(null);
            }
            if (doc.getSubjectId() != null) {
                subjectRes = subjectRepository.findById(doc.getSubjectId()).orElse(null);
            }
            return new DocumentResponse(
                    doc.getId(),
                    doc.getTitle(),
                    doc.getPages(),
                    doc.getFileType(),
                    doc.getFileUrl(),
                    levelRes != null ? levelRes.getId() : null,
                    levelRes != null ? levelRes.getName() : null,
                    subjectRes != null ? subjectRes.getId() : null,
                    subjectRes != null ? subjectRes.getName() : null,
                    doc.getUploadDate(),
                    doc.getUpdatedAt());
        }).collect(Collectors.toList());

        return new ApiResponse("success", "Lấy danh sách tài liệu đã lưu thành công", responses);
    }

    public ApiResponse getSavedDocumentsByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        // Lấy danh sách tài liệu đã lưu từ repository
        List<DocumentUser> savedDocs = documentRepository.findAllById(user.getSavedDocuments());

        List<DocumentResponse> responses = savedDocs.stream().map(doc -> {
            Level levelRes = null;
            Subject subjectRes = null;
            if (doc.getLevelId() != null) {
                levelRes = levelRepository.findById(doc.getLevelId()).orElse(null);
            }
            if (doc.getSubjectId() != null) {
                subjectRes = subjectRepository.findById(doc.getSubjectId()).orElse(null);
            }
            return new DocumentResponse(
                    doc.getId(),
                    doc.getTitle(),
                    doc.getPages(),
                    doc.getFileType(),
                    doc.getFileUrl(),
                    levelRes != null ? levelRes.getId() : null,
                    levelRes != null ? levelRes.getName() : null,
                    subjectRes != null ? subjectRes.getId() : null,
                    subjectRes != null ? subjectRes.getName() : null,
                    doc.getUploadDate(),
                    doc.getUpdatedAt());
        }).collect(Collectors.toList());

        return new ApiResponse("success", "Lấy danh sách tài liệu đã lưu thành công", responses);
    }

    public ApiResponse getSavedDocumentById(String documentId) {
        String userId = getCurrentUserId();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        if (!user.getSavedDocuments().contains(documentId)) {
            return new ApiResponse("error", "Tài liệu không tồn tại trong danh sách đã lưu", null);
        }

        DocumentUser doc = documentRepository.findById(documentId).orElse(null);
        if (doc == null) {
            return new ApiResponse("error", "Không tìm thấy tài liệu", null);
        }

        Level level = null;
        Subject subject = null;

        if (doc.getLevelId() != null) {
            level = levelRepository.findById(doc.getLevelId()).orElse(null);
        }

        if (doc.getSubjectId() != null) {
            subject = subjectRepository.findById(doc.getSubjectId()).orElse(null);
        }

        DocumentResponse response = new DocumentResponse();
        response.setId(doc.getId());
        response.setTitle(doc.getTitle());
        response.setPages(doc.getPages());
        response.setFileType(doc.getFileType());
        response.setFileUrl(doc.getFileUrl());
        response.setLevelId(level != null ? level.getId() : null);
        response.setLevelName(level != null ? level.getName() : null);
        response.setSubjectId(subject != null ? subject.getId() : null);
        response.setSubjectName(subject != null ? subject.getName() : null);
        response.setUploadDate(doc.getUploadDate());
        response.setUpdatedAt(doc.getUpdatedAt());

        return new ApiResponse("success", "Lấy thông tin tài liệu đã lưu thành công", response);
    }


    public ApiResponse unsaveDocument(String documentId) {
        String userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        boolean removed = user.getSavedDocuments().remove(documentId);

        if (!removed) {
            return new ApiResponse("error", "Tài liệu không tồn tại trong danh sách đã lưu", null);
        }

        userRepository.save(user);
        return new ApiResponse("success", "Bỏ lưu tài liệu thành công", null);
    }

    public User processOAuth2User(OAuth2UserDetails oauth2UserDetails, Provider provider) {
        try {
            User user;
            if (provider == Provider.GITHUB) {
                // Luôn sinh email từ username cho GitHub
                String username = oauth2UserDetails.getName();
                if (username == null || username.isEmpty()) {
                    username = "githubuser" + System.currentTimeMillis();
                }
                username = toAscii(username);
                String email = username + "@github.local";

                Optional<User> userOptional = findByEmail(email);
                if (userOptional.isPresent()) {
                    user = userOptional.get();
                    user.setUsername(oauth2UserDetails.getName());
                    String imageUrl = oauth2UserDetails.getImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        user.setProfilePicture(imageUrl);
                    }
                    user.setFollowerCount(0);
                    user.setFollowingCount(0);
                    user = userRepository.save(user);
                    Bio bio = new Bio();
                    bio.setUserId(user.getId());
                    bio.setCreatedAt(new Date());
                    bio.setUpdatedAt(new Date());
                    bio = bioRepository.save(bio);
                    return user;
                } else {
                    // Tạo user mới cho GitHub
                    user = new User();
                    user.setUsername(oauth2UserDetails.getName());
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setProvider(provider);
                    user.setEnabled(true);
                    user.setRole("ROLE_USER");
                    user.setFollowers(new ArrayList<>());
                    user.setFollowings(new ArrayList<>());
                    user.setPosts(new ArrayList<>());
                    user.setLikedPosts(new ArrayList<>());
                    user.setSavedPosts(new ArrayList<>());
                    user.setSavedDocuments(new ArrayList<>());
                    String imageUrl = oauth2UserDetails.getImageUrl();
                    String profilePictureUrl = (imageUrl != null && !imageUrl.isEmpty()) ? imageUrl
                            : "https://res.cloudinary.com/dxdqjj2ww/image/upload/v1747490612/default-avatar_no0qbb.png";
                    user.setProfilePicture(profilePictureUrl);
                    user.setCoverPicture(
                            "https://res.cloudinary.com/dxdqjj2ww/image/upload/v1747490613/default-cover_wheyvw.png");
                    user.setPostsCount(0);
                    user.setFollowerCount(0);
                    user.setFollowingCount(0);
                    user = userRepository.save(user);
                    Bio bio = new Bio();
                    bio.setUserId(user.getId());
                    bio.setCreatedAt(new Date());
                    bio.setUpdatedAt(new Date());
                    bio = bioRepository.save(bio);
                    return user;
                }
            }
            // Các provider khác giữ nguyên
            Optional<User> userOptional = findByEmail(oauth2UserDetails.getEmail());
            if (userOptional.isPresent()) {
                user = userOptional.get();
                if (!user.getProvider().equals(provider)) {
                    throw new RuntimeException("Email đã được đăng ký với " + user.getProvider()
                            + ". Vui lòng đăng nhập bằng " + user.getProvider());
                }
                user.setUsername(oauth2UserDetails.getName());
                if (provider == Provider.GOOGLE) {
                    user.setProfilePicture(
                            "https://res.cloudinary.com/dxdqjj2ww/image/upload/v1747490612/default-avatar_no0qbb.png");
                } else {
                    String imageUrl = oauth2UserDetails.getImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        user.setProfilePicture(imageUrl);
                    }
                }
                user.setFollowerCount(0);
                user.setFollowingCount(0);
                user = userRepository.save(user);
                Bio bio = new Bio();
                bio.setUserId(user.getId());
                bio.setCreatedAt(new Date());
                bio.setUpdatedAt(new Date());
                bio = bioRepository.save(bio);
                return user;
            } else {
                // Tạo user mới cho provider khác
                user = new User();
                user.setUsername(oauth2UserDetails.getName());
                String email = oauth2UserDetails.getEmail();
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setProvider(provider);
                user.setEnabled(true);
                user.setRole("ROLE_USER");
                user.setFollowers(new ArrayList<>());
                user.setFollowings(new ArrayList<>());
                user.setPosts(new ArrayList<>());
                user.setLikedPosts(new ArrayList<>());
                user.setSavedPosts(new ArrayList<>());
                user.setSavedDocuments(new ArrayList<>());
                String profilePictureUrl;
                if (provider == Provider.GOOGLE) {
                    profilePictureUrl = "https://res.cloudinary.com/dxdqjj2ww/image/upload/v1747490612/default-avatar_no0qbb.png";
                } else {
                    String imageUrl = oauth2UserDetails.getImageUrl();
                    profilePictureUrl = (imageUrl != null && !imageUrl.isEmpty()) ? imageUrl
                            : "https://res.cloudinary.com/dxdqjj2ww/image/upload/v1747490612/default-avatar_no0qbb.png";
                }
                user.setProfilePicture(profilePictureUrl);
                user.setCoverPicture(
                        "https://res.cloudinary.com/dxdqjj2ww/image/upload/v1747490613/default-cover_wheyvw.png");
                user.setPostsCount(0);
                user.setFollowerCount(0);
                user.setFollowingCount(0);
                user = userRepository.save(user);
                Bio bio = new Bio();
                bio.setUserId(user.getId());
                bio.setCreatedAt(new Date());
                bio.setUpdatedAt(new Date());
                bio = bioRepository.save(bio);
                return user;
            }
        } catch (Exception e) {
            log.error("Lỗi xử lý OAuth2 user: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể xử lý thông tin người dùng OAuth2: " + e.getMessage());
        }
    }

    public static String toAscii(String input) {
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("[^\\p{ASCII}]", "")
                .replaceAll("[^a-zA-Z0-9]", "")
                .toLowerCase();
    }

    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        String userId = user.getId();

        // 1. Xóa tất cả bài viết do user tạo
        postRepository.deleteByUserId(userId);

        // 2. Xử lý các post khác có liên quan (comment, reply, reaction)
        List<Post> relatedPosts = postRepository.findPostsWithUserActivity(userId);
        for (Post post : relatedPosts) {

            // Xóa comment do user viết
            if (post.getComments() != null) {
                post.getComments().removeIf(c -> c.getUserId().equals(userId));

                // Xóa reply do user viết trong từng comment
                for (Post.Comment comment : post.getComments()) {
                    if (comment.getReplies() != null) {
                        comment.getReplies().removeIf(r -> r.getUserId().equals(userId));
                    }

                    // Xóa reaction trong comments
                    if (comment.getReactions() != null) {
                        comment.getReactions().removeIf(r -> r.getUserId().equals(userId));
                    }
                }
            }

            // Xóa reaction của user
            if (post.getReactions() != null) {
                post.getReactions().removeIf(r -> r.getUserId().equals(userId));

                // Cập nhật reactionStats
                Map<String, Long> stats = post.getReactions().stream()
                    .collect(Collectors.groupingBy(Post.Reaction::getType, Collectors.counting()));

                Post.ReactionStats updatedStats = new Post.ReactionStats();
                updatedStats.setLike(stats.getOrDefault("like", 0L).intValue());
                updatedStats.setLove(stats.getOrDefault("love", 0L).intValue());
                updatedStats.setHaha(stats.getOrDefault("haha", 0L).intValue());
                updatedStats.setWow(stats.getOrDefault("wow", 0L).intValue());
                updatedStats.setSad(stats.getOrDefault("sad", 0L).intValue());
                updatedStats.setAngry(stats.getOrDefault("angry", 0L).intValue());

                post.setReactionStats(updatedStats);
                post.setReactionCount(post.getReactions().size());
            } else {
                post.setReactionStats(new Post.ReactionStats());
                post.setReactionCount(0);
            }

            post.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);
            
            postRepository.save(post);
        }

        // 3. Xóa tất cả story do user tạo
        storyRepository.deleteByUserId(userId);

        // 4. Xử lý các story khác có reaction của user
        List<Story> relatedStories = storyRepository.findByReactionsUserId(userId);
        for (Story story : relatedStories) {
            if (story.getReactions() != null) {
                story.getReactions().removeIf(r -> r.getUserId().equals(userId));
            }
            storyRepository.save(story);
        }

        // 5. Gỡ user khỏi followers/followings của người khác
        List<User> allUsers = userRepository.findAll();
        for (User u : allUsers) {
            boolean modified = false;
            if (u.getFollowers().removeIf(f -> f.equals(userId))) {
                u.setFollowerCount(Math.max(0, u.getFollowerCount() - 1));
                modified = true;
            }
            if (u.getFollowings().removeIf(f -> f.equals(userId))) {
                u.setFollowingCount(Math.max(0, u.getFollowingCount() - 1));
                modified = true;
            }
            if (modified) {
                userRepository.save(u);
            }
        }

        // 6. Xóa inquiry liên quan
        inquiryRepository.deleteByUserId(userId);

        // 7. Xóa hội thoại của người dùng
        deleteConversationsAndMessagesByUserId(userId);

        // 8. Xóa người dùng
        userRepository.delete(user);
    }

    private void deleteConversationsAndMessagesByUserId(String userId) {
        // Lấy tất cả các cuộc trò chuyện có chứa userId
        List<Conversation> conversations = conversationRepository.findByMembersContaining(userId);

        for (Conversation conversation : conversations) {
            // Xóa tất cả message thuộc conversation
            messageRepository.deleteByConversationId(conversation.getId());

            // Xóa luôn conversation
            conversationRepository.delete(conversation);
        }
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        // Mã hóa và cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Google Calendar methods
    public void saveGoogleCalendarTokens(String userId, String accessToken, String refreshToken, LocalDateTime expiry) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        user.setGoogleCalendarAccessToken(accessToken);
        user.setGoogleCalendarRefreshToken(refreshToken);
        user.setGoogleCalendarTokenExpiry(expiry);
        user.setGoogleCalendarConnected(true);

        userRepository.save(user);
    }

    public void disconnectGoogleCalendar(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        user.setGoogleCalendarAccessToken(null);
        user.setGoogleCalendarRefreshToken(null);
        user.setGoogleCalendarTokenExpiry(null);
        user.setGoogleCalendarConnected(false);

        userRepository.save(user);
    }

    public boolean isGoogleCalendarConnected(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return user.isGoogleCalendarConnected();
    }

    public String getGoogleCalendarAccessToken(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return user.getGoogleCalendarAccessToken();
    }

    public String getGoogleCalendarRefreshToken(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return user.getGoogleCalendarRefreshToken();
    }

    public LocalDateTime getGoogleCalendarTokenExpiry(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return user.getGoogleCalendarTokenExpiry();
    }

    public User deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Xóa bio nếu có
        bioRepository.findByUserId(userId).ifPresent(bio -> {
            bioRepository.delete(bio);
        });

        userRepository.delete(user);
        return user;
    }
}