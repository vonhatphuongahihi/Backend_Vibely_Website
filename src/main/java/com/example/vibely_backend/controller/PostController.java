package com.example.vibely_backend.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.vibely_backend.dto.response.PostDTO;
import com.example.vibely_backend.dto.response.StoryDTO;
import com.example.vibely_backend.dto.response.UserMiniDTO;
import com.example.vibely_backend.entity.Post;
import com.example.vibely_backend.entity.Story;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.PostRepository;
import com.example.vibely_backend.repository.StoryRepository;
import com.example.vibely_backend.repository.UserRepository;
import com.example.vibely_backend.service.CloudinaryService;
import com.example.vibely_backend.service.PostService;
import com.example.vibely_backend.service.StoryService;
import com.example.vibely_backend.utils.ResponseHandler;

@RestController
@RequestMapping("/users")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @Autowired
    private StoryService storyService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserRepository userRepository;

    // Tạo bài viết
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile file) {
        try {
            System.out.println("File: " + (file != null ? file.getOriginalFilename() : "null"));
            System.out.println("Content: " + content);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            String mediaUrl = null;
            String mediaType = null;

            // Kiểm tra nếu có file thì upload lên Cloudinary
            if (file != null) {
                Map<String, Object> uploadResult = cloudinaryService.uploadFile(file);
                if (uploadResult == null || !uploadResult.containsKey("secure_url")) {
                    return ResponseHandler.response(HttpStatus.BAD_REQUEST, "Lỗi khi tải lên tệp.");
                }

                mediaUrl = (String) uploadResult.get("secure_url");
                String contentType = file.getContentType();
                if (contentType == null) {
                    mediaType = "image"; // Default to image if content type is null
                } else {
                    mediaType = contentType.startsWith("video") ? "video" : "image";
                }
            } else if (content == null || content.trim().isEmpty()) {
                // Nếu không có file và không có content, trả về lỗi
                return ResponseHandler.response(HttpStatus.BAD_REQUEST, "Bài viết phải có nội dung hoặc đính kèm file");
            }

            // Get the user by email
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Tạo bài viết mới với các thông số ban đầu
            Post newPost = new Post();
            newPost.setUserId(user.getId());
            newPost.setContent(content);
            newPost.setMediaUrl(mediaUrl);
            newPost.setMediaType(mediaType);
            newPost.setReactionCount(0);
            newPost.setCommentCount(0);
            newPost.setShareCount(0);

            Post.ReactionStats reactionStats = new Post.ReactionStats();
            reactionStats.setLike(0);
            reactionStats.setLove(0);
            reactionStats.setHaha(0);
            reactionStats.setWow(0);
            reactionStats.setSad(0);
            reactionStats.setAngry(0);
            newPost.setReactionStats(reactionStats);

            newPost.setCreatedAt(new Date());
            newPost.setUpdatedAt(new Date());

            Post savedPost = postRepository.save(newPost);
            return ResponseHandler.response(HttpStatus.CREATED, "Tạo bài viết thành công", savedPost);

        } catch (IOException e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Tạo bài viết thất bại", e.getMessage());
        }
    }

    // Tạo story
    @PostMapping("/story")
    public ResponseEntity<?> createStory(@RequestParam("file") MultipartFile file) {
        System.out.println("createStorycreateStorycreateStorycreateStorycreateStorycreateStory: " + file);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Get the user by email
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (file == null) {
                return ResponseHandler.response(HttpStatus.BAD_REQUEST, "Cần tải file lên để tạo story");
            }

            String contentType = file.getContentType();
            if (contentType == null) {
                return ResponseHandler.response(HttpStatus.BAD_REQUEST, "Không thể xác định loại file");
            }

            boolean isVideo = contentType.startsWith("video");
            boolean isImage = contentType.startsWith("image");

            if (!isVideo && !isImage) {
                return ResponseHandler.response(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ ảnh hoặc video");
            }

            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file);
            if (uploadResult == null || !uploadResult.containsKey("secure_url")) {
                return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi tải file lên");
            }
            Story newStory = new Story();

            newStory.setUserId(user.getId());
            newStory.setMediaUrl((String) uploadResult.get("secure_url"));
            newStory.setMediaType(isVideo ? "video" : "image");
            newStory.setCreatedAt(new Date());
            newStory.setUpdatedAt(new Date());

            Story savedStory = storyRepository.save(newStory);
            StoryDTO storyDTO = new StoryDTO(savedStory, new UserMiniDTO(user));
            return ResponseHandler.response(HttpStatus.CREATED, "Tạo story thành công", storyDTO);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Tạo story thất bại", e.getMessage());
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        try {
            List<PostDTO> posts = postService.getAllPosts();
            posts.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
            return ResponseHandler.response(HttpStatus.OK, "Lấy danh sách bài viết thành công", posts);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi lấy bài viết", e.getMessage());
        }
    }

    // Lấy tất cả story
    @GetMapping("/story")
    public ResponseEntity<?> getAllStories() {
        try {
            List<StoryDTO> stories = storyService.getAllStories();
            stories.sort((s1, s2) -> {
                Date date1 = s1.getCreatedAt();
                Date date2 = s2.getCreatedAt();
                if (date1 == null && date2 == null)
                    return 0;
                if (date1 == null)
                    return 1;
                if (date2 == null)
                    return -1;
                return date2.compareTo(date1);
            });
            return ResponseHandler.response(HttpStatus.OK, "Lấy tất cả story thành công", stories);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Lấy tất cả story thất bại",
                    e.getMessage());
        }
    }

    // Lấy bài viết theo ID người dùng
    @GetMapping("/posts/user/{id}")
    public ResponseEntity<?> getPostByUserId(@PathVariable String id) {
        try {
            if (id == null || id.isEmpty()) {
                return ResponseHandler.response(HttpStatus.BAD_REQUEST, "Yêu cầu mã người dùng để lấy bài viết");
            }
            List<PostDTO> posts = postService.getPostsByUserId(id);
            return ResponseHandler.response(HttpStatus.OK, "Lấy bài viết theo ID người dùng thành công", posts);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lấy bài viết theo ID người dùng thất bại", e.getMessage());
        }
    }

    // React bài viết
    @PostMapping("posts/react/{postId}")
    public ResponseEntity<?> reactPost(@PathVariable String postId, @RequestBody Map<String, String> body) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String type = body.get("type");
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();
            if (post.getReactionStats() == null) {
                post.setReactionStats(new Post.ReactionStats());
            }
            // Tìm reaction của user nếu có
            int existingReactionIndex = findExistingReactionIndex(post, user.getId());
            logger.info("existingReactionIndex: " + existingReactionIndex);

            // This variable is used in the response message
            String action = processReaction(post, existingReactionIndex, type, user);

            // Cập nhật reactionCount
            if (post.getReactions() != null) {
                post.setReactionCount(post.getReactions().size());
            } else {
                post.setReactionCount(0);
            }

            post.setUpdatedAt(new Date());

            logger.info("Logging updated post: " + post);

            Post updatedPost = postRepository.save(post);

            Map<String, Object> responseData = Map.<String, Object>of(
                    "reactionStats", updatedPost.getReactionStats(),
                    "reactions", updatedPost.getReactions());

            return ResponseHandler.response(HttpStatus.OK, action, responseData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Thích bài viết thất bại",
                    e.getMessage());
        }
    }

    private int findExistingReactionIndex(Post post, String userId) {
        if (post.getReactions() == null || userId == null) {
            return -1;
        }

        for (int i = 0; i < post.getReactions().size(); i++) {
            Post.Reaction reaction = post.getReactions().get(i);
            if (userId.equals(reaction.getUserId())) {
                return i;
            }
        }

        return -1;
    }

    private String processReaction(Post post, int existingReactionIndex, String type, User user) {
        String action;

        if (existingReactionIndex != -1) {
            // Người dùng đã từng react
            Post.Reaction existingReaction = post.getReactions().get(existingReactionIndex);
            String oldType = existingReaction.getType();

            if (oldType.equalsIgnoreCase(type)) {
                // Bấm lại cùng 1 loại => bỏ react
                post.getReactions().remove(existingReactionIndex);
                decrementReactionStat(post, oldType);
                action = "Đã bỏ " + oldType + " bài viết";
            } else {
                // Đổi sang loại khác
                decrementReactionStat(post, oldType);
                existingReaction.setType(type);
                existingReaction.setCreatedAt(new Date()); // Cập nhật lại thời gian
                incrementReactionStat(post, type);
                action = "Đã đổi phản ứng từ " + oldType + " sang " + type;
            }
        } else {
            // Người dùng chưa từng react
            if (post.getReactions() == null) {
                post.setReactions(new ArrayList<>());
            }

            Post.Reaction newReaction = new Post.Reaction();
            newReaction.setUserId(user.getId());
            newReaction.setType(type);
            newReaction.setCreatedAt(new Date());

            post.getReactions().add(newReaction);
            incrementReactionStat(post, type);
            action = "Đã " + type + " bài viết";
        }

        return action;
    }

    private void incrementReactionStat(Post post, String type) {
        switch (type.toLowerCase()) {
            case "like":
                post.getReactionStats().setLike(post.getReactionStats().getLike() + 1);
                break;
            case "love":
                post.getReactionStats().setLove(post.getReactionStats().getLove() + 1);
                break;
            case "haha":
                post.getReactionStats().setHaha(post.getReactionStats().getHaha() + 1);
                break;
            case "wow":
                post.getReactionStats().setWow(post.getReactionStats().getWow() + 1);
                break;
            case "sad":
                post.getReactionStats().setSad(post.getReactionStats().getSad() + 1);
                break;
            case "angry":
                post.getReactionStats().setAngry(post.getReactionStats().getAngry() + 1);
                break;
        }
    }

    private void decrementReactionStat(Post post, String type) {
        switch (type.toLowerCase()) {
            case "like":
                post.getReactionStats().setLike(Math.max(0, post.getReactionStats().getLike() - 1));
                break;
            case "love":
                post.getReactionStats().setLove(Math.max(0, post.getReactionStats().getLove() - 1));
                break;
            case "haha":
                post.getReactionStats().setHaha(Math.max(0, post.getReactionStats().getHaha() - 1));
                break;
            case "wow":
                post.getReactionStats().setWow(Math.max(0, post.getReactionStats().getWow() - 1));
                break;
            case "sad":
                post.getReactionStats().setSad(Math.max(0, post.getReactionStats().getSad() - 1));
                break;
            case "angry":
                post.getReactionStats().setAngry(Math.max(0, post.getReactionStats().getAngry() - 1));
                break;
        }
    }

    // Thả tym story
    @PostMapping("/story/react/{storyId}")
    public ResponseEntity<?> reactStory(@PathVariable String storyId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Get the user by email
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Story> storyOpt = storyRepository.findById(storyId);
            if (storyOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy story");
            }

            Story story = storyOpt.get();

            // Khởi tạo reactionStats nếu chưa có
            if (story.getReactionStats() == null) {
                story.setReactionStats(new Story.ReactionStats());
            }

            // Tìm reaction của user nếu có
            int existingReactionIndex = -1;
            if (story.getReactions() != null) {
                for (int i = 0; i < story.getReactions().size(); i++) {
                    if (story.getReactions().get(i).getUserId().equals(user.getId())) {
                        existingReactionIndex = i;
                        break;
                    }
                }
            } else {
                story.setReactions(new ArrayList<>());
            }

            String action;
            if (existingReactionIndex != -1) {
                // Nếu user đã tym => bỏ tym
                story.getReactions().remove(existingReactionIndex);
                story.getReactionStats().setTym(Math.max(0, story.getReactionStats().getTym() - 1));
                action = "Bỏ thích story";
            } else {
                // Nếu chưa tym => thêm tym
                Story.Reaction newStoryReaction = new Story.Reaction();
                newStoryReaction.setUserId(user.getId());
                newStoryReaction.setCreatedAt(new Date());
                story.getReactions().add(newStoryReaction);
                story.getReactionStats().setTym(story.getReactionStats().getTym() + 1);
                action = "Đã thích story";
            }

            story.setUpdatedAt(new Date());
            Story updatedStory = storyRepository.save(story);
            StoryDTO storyDTO = new StoryDTO(updatedStory, new UserMiniDTO(user));
            return ResponseHandler.response(HttpStatus.OK, action, storyDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Thả tym story thất bại", e.getMessage());
        }
    }

    // Xóa story
    @DeleteMapping("/story/{storyId}")
    public ResponseEntity<?> deleteStory(@PathVariable String storyId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            // Get the user by email
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Story> storyOpt = storyRepository.findById(storyId);
            if (storyOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy story");
            }

            Story story = storyOpt.get();

            // Kiểm tra xem người dùng có phải là người tạo story không
            if (!story.getUserId().equals(user.getId())) {
                return ResponseHandler.response(HttpStatus.FORBIDDEN, "Bạn không có quyền xóa story này");
            }

            // Xóa story
            storyRepository.delete(story);
            return ResponseHandler.response(HttpStatus.OK, "Xóa story thành công");

        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Xóa story thất bại", e.getMessage());
        }
    }

    // Bình luận bài viết
    @PostMapping("/posts/comments/{postId}")
    public ResponseEntity<?> addCommentToPost(@PathVariable String postId, @RequestBody Map<String, String> body) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            String text = body.get("text");

            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();
            System.out.println("====STEP 0=====: " + post.getComments());

            if (post.getComments() == null) {
                post.setComments(new ArrayList<>());
            }

            Post.Comment newComment = new Post.Comment();
            // Get the user by email
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            newComment.setUserId(user.getId());
            newComment.setText(text);
            newComment.setCreatedAt(new Date());

            post.getComments().add(newComment);
            post.setCommentCount(post.getCommentCount() + 1);
            post.setUpdatedAt(new Date());

            newComment.setId(UUID.randomUUID().toString());
            System.out.println("====STEP 0.1=====: " + newComment.getId());

            Post updatedPost = postRepository.save(post);
            return ResponseHandler.response(HttpStatus.CREATED, "Bình luận bài viết thành công", updatedPost);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Bình luận bài viết thất bại",
                    e.getMessage());
        }
    }

    // Phản hồi bình luận
    @PostMapping("/posts/reply/{postId}")
    public ResponseEntity<?> addReplyToPost(@PathVariable String postId, @RequestBody Map<String, String> body) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            String commentId = body.get("commentId");
            String text = body.get("text");

            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();
            if (post.getComments() == null) {
                post.setComments(new ArrayList<>());
            }

            // Tìm comment
            Post.Comment comment = null;
            for (Post.Comment c : post.getComments()) {
                if (c.getId() != null && c.getId().equals(commentId)) {
                    comment = c;
                    break;
                }
            }

            if (comment == null) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận");
            }

            if (comment.getReplies() == null) {
                comment.setReplies(new ArrayList<>());
            }

            Post.Reply newReply = new Post.Reply();
            // Get the user by email
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            newReply.setId(UUID.randomUUID().toString());
            newReply.setUserId(user.getId());
            newReply.setText(text);
            newReply.setCreatedAt(new Date());

            comment.getReplies().add(newReply);
            post.setUpdatedAt(new Date());

            Post updatedPost = postRepository.save(post);

            // Trả về thông tin reply mới với user_id
            Map<String, Object> replyData = new HashMap<>();
            replyData.put("id", newReply.getId());
            replyData.put("user_id", newReply.getUserId());
            replyData.put("text", newReply.getText());
            replyData.put("created_at", newReply.getCreatedAt());

            return ResponseHandler.response(HttpStatus.CREATED, "Phản hồi bình luận thành công", replyData);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Phản hồi bình luận thất bại",
                    e.getMessage());
        }
    }

    // Chia sẻ bài viết
    @PostMapping("/posts/share/{postId}")
    public ResponseEntity<?> sharePost(@PathVariable String postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();

            if (post.getShare() == null) {
                post.setShare(new ArrayList<>());
            }

            // Get the user by email
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Kiểm tra xem user đã share chưa
            boolean hasShared = post.getShare().contains(user.getId());

            if (!hasShared) {
                post.getShare().add(user.getId());
                post.setShareCount(post.getShareCount() + 1);
                post.setUpdatedAt(new Date());
            }

            Post updatedPost = postRepository.save(post);
            return ResponseHandler.response(HttpStatus.OK, "Chia sẻ bài viết thành công", updatedPost);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Chia sẻ bài viết thất bại",
                    e.getMessage());
        }
    }

    // Xóa bài viết
    @DeleteMapping("/posts/delete/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String postId) {
        try {
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();
            postRepository.deleteById(postId);
            return ResponseHandler.response(HttpStatus.OK, "Xóa bài viết thành công", post);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Xóa bài viết thất bại", e.getMessage());
        }
    }

    // Hỗ trợ GET cho xóa bài viết
    @GetMapping("/{postId}/delete")
    public ResponseEntity<?> deletePostWithGet(@PathVariable String postId) {
        return deletePost(postId);
    }

    // Xóa bình luận
    @DeleteMapping("/posts/deleteComment/{postId}/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String postId, @PathVariable String commentId) {
        try {
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();

            // Tìm comment
            int commentIndex = -1;
            for (int i = 0; i < post.getComments().size(); i++) {
                if (post.getComments().get(i).getId().equals(commentId)) {
                    commentIndex = i;
                    break;
                }
            }

            System.out.println("====STEP 0.1=====: " + commentIndex);

            if (commentIndex == -1) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận");
            }

            post.setCommentCount(post.getCommentCount() - 1);
            post.getComments().remove(commentIndex);
            post.setUpdatedAt(new Date());

            Post updatedPost = postRepository.save(post);
            return ResponseHandler.response(HttpStatus.OK, "Xóa bình luận thành công", updatedPost);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Xóa bình luận thất bại", e.getMessage());
        }
    }

    // Xóa phản hồi
    @DeleteMapping("/posts/deleteReply/{postId}/{commentId}/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable String postId, @PathVariable String commentId,
            @PathVariable String replyId) {
        try {
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();

            // Tìm comment
            int commentIndex = -1;
            for (int i = 0; i < post.getComments().size(); i++) {
                if (post.getComments().get(i).getId() != null && post.getComments().get(i).getId().equals(commentId)) {
                    commentIndex = i;
                    break;
                }
            }

            if (commentIndex == -1) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận");
            }

            // Tìm reply
            int replyIndex = -1;
            for (int i = 0; i < post.getComments().get(commentIndex).getReplies().size(); i++) {
                if (post.getComments().get(commentIndex).getReplies().get(i).getId() != null
                        && post.getComments().get(commentIndex).getReplies().get(i).getId().equals(replyId)) {
                    replyIndex = i;
                    break;
                }
            }

            if (replyIndex == -1) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy phản hồi");
            }

            post.getComments().get(commentIndex).getReplies().remove(replyIndex);
            post.setUpdatedAt(new Date());

            Post updatedPost = postRepository.save(post);
            return ResponseHandler.response(HttpStatus.OK, "Xóa phản hồi thành công", updatedPost);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Xóa phản hồi thất bại", e.getMessage());
        }
    }

    // Lấy bài viết theo ID
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getSinglePost(@PathVariable String postId) {
        try {
            if (postId == null || postId.isEmpty()) {
                return ResponseHandler.response(HttpStatus.BAD_REQUEST, "Yêu cầu mã bài viết");
            }

            PostDTO post = postService.getPostById(postId);
            if (post == null) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            return ResponseHandler.response(HttpStatus.OK, "Lấy bài viết thành công", post);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Lấy bài viết thất bại", e.getMessage());
        }
    }

    // Chỉnh sửa bài viết
    @PutMapping("/posts/edit/{postId}")
    public ResponseEntity<?> editPost(
            @PathVariable String postId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false, defaultValue = "false") boolean removeMedia) {
        System.out.println("====Edit Post Request=====");
        System.out.println("PostId: " + postId);
        System.out.println("Content: " + content);
        System.out.println("File: " + (file != null ? file.getOriginalFilename() : "null"));
        System.out.println("File size: " + (file != null ? file.getSize() : "null"));
        System.out.println("Remove Media: " + removeMedia);

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post existingPost = postOpt.get();
            System.out.println("====Existing Post Media URL BEFORE=====: " + existingPost.getMediaUrl());
            System.out.println("====Existing Post Media Type BEFORE=====: " + existingPost.getMediaType());

            // Kiểm tra xem người dùng có phải là chủ bài viết không
            if (!existingPost.getUserId().equals(user.getId())) {
                return ResponseHandler.response(HttpStatus.FORBIDDEN, "Bạn không có quyền chỉnh sửa bài viết này");
            }

            // Cập nhật nội dung text nếu có
            if (content != null) {
                System.out.println("====Updating content=====");
                existingPost.setContent(content);
            }

            // Xử lý media
            if (removeMedia) {
                System.out.println("====Removing media=====");
                // Xóa media hiện tại
                if (existingPost.getMediaUrl() != null) {
                    try {
                        // Có thể thêm logic xóa file trên Cloudinary nếu cần
                        // cloudinaryService.deleteFile(existingPost.getMediaUrl());
                    } catch (Exception e) {
                        System.out.println("Warning: Could not delete old media file: " + e.getMessage());
                    }
                }
                existingPost.setMediaUrl(null);
                existingPost.setMediaType(null);
            } else if (file != null && !file.isEmpty()) {
                System.out.println("====Uploading new file=====");
                System.out.println("File content type: " + file.getContentType());

                // Upload file mới
                Map<String, Object> uploadResult = null;
                try {
                    uploadResult = cloudinaryService.uploadFile(file);
                    System.out.println("====Upload result=====: " + uploadResult);
                } catch (IOException uploadException) {
                    System.out.println("====Upload failed=====: " + uploadException.getMessage());
                    uploadException.printStackTrace();
                    return ResponseHandler.response(HttpStatus.BAD_REQUEST,
                            "Lỗi khi tải lên tệp mới: " + uploadException.getMessage());
                }

                if (uploadResult == null || !uploadResult.containsKey("secure_url")) {
                    System.out.println("====Upload result is null or missing secure_url=====");
                    return ResponseHandler.response(HttpStatus.BAD_REQUEST,
                            "Lỗi khi tải lên tệp mới - không nhận được URL");
                }

                // Xóa file cũ nếu có (optional - có thể bỏ comment để tiết kiệm storage)
                if (existingPost.getMediaUrl() != null) {
                    try {
                        // cloudinaryService.deleteFile(existingPost.getMediaUrl());
                        System.out.println("====Would delete old file=====: " + existingPost.getMediaUrl());
                    } catch (Exception e) {
                        System.out.println("Warning: Could not delete old media file: " + e.getMessage());
                    }
                }

                // Cập nhật với file mới
                String mediaUrl = (String) uploadResult.get("secure_url");
                String contentType = file.getContentType();
                String mediaType = (contentType != null && contentType.startsWith("video")) ? "video" : "image";

                System.out.println("====Setting new media URL=====: " + mediaUrl);
                System.out.println("====Setting new media type=====: " + mediaType);

                existingPost.setMediaUrl(mediaUrl);
                existingPost.setMediaType(mediaType);

                System.out.println("====Post media URL AFTER setting=====: " + existingPost.getMediaUrl());
                System.out.println("====Post media type AFTER setting=====: " + existingPost.getMediaType());
            }

            // Kiểm tra bài viết có content hoặc media
            if ((existingPost.getContent() == null || existingPost.getContent().trim().isEmpty()) &&
                    existingPost.getMediaUrl() == null) {
                return ResponseHandler.response(HttpStatus.BAD_REQUEST, "Bài viết phải có nội dung hoặc đính kèm file");
            }

            existingPost.setUpdatedAt(new Date());

            System.out.println("====Saving post with media URL=====: " + existingPost.getMediaUrl());
            System.out.println("====Saving post with media type=====: " + existingPost.getMediaType());

            Post savedPost = postRepository.save(existingPost);

            System.out.println("====Saved post media URL=====: " + savedPost.getMediaUrl());
            System.out.println("====Saved post media type=====: " + savedPost.getMediaType());
            System.out.println("====Saved post ID=====: " + savedPost.getId());

            return ResponseHandler.response(HttpStatus.OK, "Chỉnh sửa bài viết thành công", savedPost);
        } catch (Exception e) {
            System.out.println("====Exception during edit=====: " + e.getMessage());
            e.printStackTrace();
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Chỉnh sửa bài viết thất bại: " + e.getMessage());
        }
    }

    // Thích bình luận
    @PostMapping("/posts/reactComment/{postId}/{commentId}")
    public ResponseEntity<?> reactComment(@PathVariable String postId, @PathVariable String commentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();

            // Tìm comment
            Post.Comment comment = post.getComments().stream()
                    .filter(c -> c.getId() != null && c.getId().equals(commentId))
                    .findFirst()
                    .orElse(null);

            if (comment == null) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bình luận");
            }
            // Tìm reaction của user nếu có
            int existingReactionIndex = -1;
            if (comment.getReactions() != null) {
                for (int i = 0; i < comment.getReactions().size(); i++) {
                    Post.Reaction reaction = comment.getReactions().get(i);
                    if (reaction != null && reaction.getUserId() != null &&
                            reaction.getUserId().equals(user.getId())) {
                        existingReactionIndex = i;
                        break;
                    }
                }
            } else {
                comment.setReactions(new ArrayList<>());
            }

            String action = "";

            if (existingReactionIndex != -1) {
                // Nếu user đã phản ứng => bỏ phản ứng
                comment.getReactions().remove(existingReactionIndex);
                action = "Bỏ react";
            } else {
                // Nếu chưa phản ứng => thêm phản ứng
                Post.Reaction newCommentReaction = new Post.Reaction();
                newCommentReaction.setUserId(user.getId());
                newCommentReaction.setType("like");
                newCommentReaction.setCreatedAt(new Date());
                comment.getReactions().add(newCommentReaction);
                action = "React thành công";
            }

            post.setUpdatedAt(new Date());
            Post updatedPost = postRepository.save(post);
            return ResponseHandler.response(HttpStatus.CREATED, action, updatedPost);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "React thất bại", e.getMessage());
        }
    }

    // Debug endpoint để kiểm tra thông tin post
    @GetMapping("/posts/debug/{postId}")
    public ResponseEntity<?> debugPost(@PathVariable String postId) {
        try {
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();

            System.out.println("====DEBUG POST=====");
            System.out.println("Post ID: " + post.getId());
            System.out.println("Post Content: " + post.getContent());
            System.out.println("Post Media URL: " + post.getMediaUrl());
            System.out.println("Post Media Type: " + post.getMediaType());
            System.out.println("Post Created At: " + post.getCreatedAt());
            System.out.println("Post Updated At: " + post.getUpdatedAt());
            System.out.println("Post User ID: " + post.getUserId());

            Map<String, Object> debugInfo = Map.of(
                    "id", post.getId(),
                    "content", post.getContent() != null ? post.getContent() : "null",
                    "mediaUrl", post.getMediaUrl() != null ? post.getMediaUrl() : "null",
                    "mediaType", post.getMediaType() != null ? post.getMediaType() : "null",
                    "createdAt", post.getCreatedAt(),
                    "updatedAt", post.getUpdatedAt(),
                    "userId", post.getUserId());

            return ResponseHandler.response(HttpStatus.OK, "Debug info", debugInfo);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Debug failed", e.getMessage());
        }
    }
}