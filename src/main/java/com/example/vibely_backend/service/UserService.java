package com.example.vibely_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import com.example.vibely_backend.dto.response.MutualFriendResponse;
import com.example.vibely_backend.dto.response.SimpleUserResponse;
import com.example.vibely_backend.dto.response.UserProfileResponse;
import com.example.vibely_backend.entity.Bio;
import com.example.vibely_backend.entity.DocumentUser;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.entity.Provider;
import com.example.vibely_backend.service.oauth2.OAuth2UserDetails;
import com.example.vibely_backend.repository.UserRepository;
import com.example.vibely_backend.repository.BioRepository;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.Date;

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BioRepository bioRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

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

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            log.warn("Username đã tồn tại: {}", user.getUsername());
            throw new RuntimeException("Username đã tồn tại");
        }
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
        user.setProfilePicture("");
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

    public User updateUserProfile(
            String userId,
            UserProfileUpdateRequest request,
            String profilePictureUrl,
            String coverPictureUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Cập nhật thông tin từ request
        if (request.getUsername() != null)
            user.setUsername(request.getUsername());
        if (request.getEmail() != null)
            user.setEmail(request.getEmail());
        if (request.getGender() != null)
            user.setGender(request.getGender());

        // Nếu dateOfBirth là String thì cần parse:
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        // Cập nhật ảnh nếu có
        if (profilePictureUrl != null)
            user.setProfilePicture(profilePictureUrl);
        if (coverPictureUrl != null)
            user.setCoverPicture(coverPictureUrl);

        return userRepository.save(user);
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

        if (currentUser.getFollowings().contains(userToFollow)) {
            return new ApiResponse("error", "Bạn đã theo dõi người dùng này", null);
        }

        currentUser.getFollowings().add(userToFollow);
        userToFollow.getFollowers().add(currentUser);

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

        boolean isFollowing = currentUser.getFollowings().stream()
                .anyMatch(user -> user.getId().equals(userIdToUnfollow));

        if (!isFollowing) {
            return new ApiResponse("error", "Bạn chưa theo dõi người dùng này", null);
        }

        currentUser.getFollowers().remove(userToUnfollow);
        currentUser.getFollowings().remove(userToUnfollow);
        userToUnfollow.getFollowers().remove(currentUser);
        userToUnfollow.getFollowings().remove(currentUser);

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

        if (!sender.getFollowings().contains(currentUser)) {
            return new ApiResponse("error", "Không tìm thấy yêu cầu kết bạn", null);
        }

        sender.getFollowings().remove(currentUser);
        currentUser.getFollowers().remove(sender);

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

        List<SimpleUserResponse> requests = userRepository
                .findAllById(currentUser.getFollowers().stream()
                        .filter(f -> !currentUser.getFollowings().contains(f))
                        .map(User::getId)
                        .toList())
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

        excludedIds.addAll(
                currentUser.getFollowers().stream()
                        .map(User::getId)
                        .collect(Collectors.toSet()));

        excludedIds.addAll(
                currentUser.getFollowings().stream()
                        .map(User::getId)
                        .collect(Collectors.toSet()));

        List<SimpleUserResponse> allUsers = userRepository.findAllSimpleUsers();

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
        Set<String> followingIds = profileUser.getFollowings().stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        // Lọc follower nào cũng đang được follow lại (bạn chung)
        List<MutualFriendResponse> mutualFriends = profileUser.getFollowers().stream()
                .filter(follower -> followingIds.contains(follower.getId()))
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
        List<MutualFriendResponse> userInfos = userRepository.findAllUserFull();

        return new ApiResponse("success", "Lấy tất cả người dùng thành công", userInfos);
    }

    public ApiResponse getUserProfile(String userId) {
        String loggedInUserId = getCurrentUserId();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        User user = optionalUser.get();
        Bio bio = user.getBio();

        BioResponse bioResponse = bio != null ? new BioResponse(
                bio.getBioText(),
                bio.getLiveIn(),
                bio.getRelationship(),
                bio.getWorkplace(),
                bio.getEducation(),
                bio.getPhone(),
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

        List<DocumentUser> filteredDocs = user.getSavedDocuments().stream()
                .filter(doc -> (query == null || query.isBlank()
                        || doc.getTitle().toLowerCase().contains(query.toLowerCase())) &&
                        (level == null || level.isBlank() || doc.getLevel().getId().equals(level)) &&
                        (subject == null || subject.isBlank() || doc.getSubject().getId().equals(subject)))
                .toList();

        return new ApiResponse("success", "Lấy danh sách tài liệu đã lưu thành công", filteredDocs);
    }

    public ApiResponse getSavedDocumentById(String documentId) {
        String userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        Optional<DocumentUser> doc = user.getSavedDocuments().stream()
                .filter(d -> d.getId().equals(documentId))
                .findFirst();

        if (doc.isEmpty()) {
            return new ApiResponse("error", "Tài liệu không tồn tại trong danh sách đã lưu", null);
        }

        return new ApiResponse("success", "Lấy thông tin tài liệu đã lưu thành công", doc.get());
    }

    public ApiResponse unsaveDocument(String documentId) {
        String userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return new ApiResponse("error", "Người dùng không tồn tại", null);
        }

        boolean removed = user.getSavedDocuments().removeIf(doc -> doc.getId().equals(documentId));

        if (!removed) {
            return new ApiResponse("error", "Tài liệu không tồn tại trong danh sách đã lưu", null);
        }

        userRepository.save(user);
        return new ApiResponse("success", "Bỏ lưu tài liệu thành công", null);
    }

    public User processOAuth2User(OAuth2UserDetails oauth2UserDetails, Provider provider) {
        try {
            Optional<User> userOptional = findByEmail(oauth2UserDetails.getEmail());
            User user;

            if (userOptional.isPresent()) {
                user = userOptional.get();
                if (!user.getProvider().equals(provider)) {
                    throw new RuntimeException("Email đã được đăng ký với " + user.getProvider()
                            + ". Vui lòng đăng nhập bằng " + user.getProvider());
                }
                user.setUsername(oauth2UserDetails.getName());
                // Cập nhật ảnh đại diện nếu có
                String imageUrl = oauth2UserDetails.getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    user.setProfilePicture(imageUrl);
                }
                return userRepository.save(user);
            } else {
                // Tạo user mới
                user = new User();
                user.setUsername(oauth2UserDetails.getName());
                user.setEmail(oauth2UserDetails.getEmail());
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

                // Sử dụng ảnh đại diện từ OAuth2 provider
                String imageUrl = oauth2UserDetails.getImageUrl();
                user.setProfilePicture(imageUrl != null && !imageUrl.isEmpty() ? imageUrl
                        : "https://res.cloudinary.com/dxav6uhnu/image/upload/v1715529600/vibely/default-avatar.png");

                user.setCoverPicture(
                        "https://res.cloudinary.com/dxav6uhnu/image/upload/v1715529600/vibely/default-cover.png");
                user.setPostsCount(0);
                user.setFollowerCount(0);
                user.setFollowingCount(0);
                user.setBio(null);

                // Lưu user trước
                user = userRepository.save(user);

                // Tạo và lưu bio
                Bio bio = new Bio();
                bio.setUser(user);
                bio.setCreatedAt(new Date());
                bio.setUpdatedAt(new Date());
                bio = bioRepository.save(bio);

                // Cập nhật user với bio
                user.setBio(bio);
                return userRepository.save(user);
            }
        } catch (Exception e) {
            log.error("Lỗi: {}", e.getMessage());
            throw new RuntimeException("Không thể xử lý thông tin người dùng OAuth2: " + e.getMessage());
        }
    }

}