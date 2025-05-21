package com.example.vibely_backend.controller;

import com.example.vibely_backend.entity.Post;
import com.example.vibely_backend.entity.Story;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.dto.response.PostDTO;
import com.example.vibely_backend.service.PostService;
import com.example.vibely_backend.repository.PostRepository;
import com.example.vibely_backend.repository.StoryRepository;
import com.example.vibely_backend.repository.UserRepository;
import com.example.vibely_backend.service.CloudinaryService;
import com.example.vibely_backend.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

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
            newPost.setUser(user);
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

            newStory.setUser(user);
            newStory.setMediaUrl((String) uploadResult.get("secure_url"));
            newStory.setMediaType(isVideo ? "video" : "image");
            newStory.setCreatedAt(new Date());
            newStory.setUpdatedAt(new Date());

            Story savedStory = storyRepository.save(newStory);
            return ResponseHandler.response(HttpStatus.CREATED, "Tạo story thành công", savedStory);

        } catch (IOException e) {
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
            List<Story> stories = storyRepository.findAll();
            stories.sort((s1, s2) -> s2.getCreatedAt().compareTo(s1.getCreatedAt()));
            return ResponseHandler.response(HttpStatus.OK, "Lấy tất cả story thành công", stories);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Lấy tất cả story thất bại",
                    e.getMessage());
        }
    }

    // Lấy bài viết theo ID người dùng
    @GetMapping("posts/user/{userId}")
    public ResponseEntity<?> getPostByUserId(@PathVariable String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return ResponseHandler.response(HttpStatus.BAD_REQUEST, "Yêu cầu mã người dùng để lấy bài viết");
            }
            List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);
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
            User user = reaction.getUser();
    
            if (user != null && userId.equals(user.getId())) {
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
            newReaction.setUser(user);
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
            case "like": post.getReactionStats().setLike(post.getReactionStats().getLike() + 1); break;
            case "love": post.getReactionStats().setLove(post.getReactionStats().getLove() + 1); break;
            case "haha": post.getReactionStats().setHaha(post.getReactionStats().getHaha() + 1); break;
            case "wow": post.getReactionStats().setWow(post.getReactionStats().getWow() + 1); break;
            case "sad": post.getReactionStats().setSad(post.getReactionStats().getSad() + 1); break;
            case "angry": post.getReactionStats().setAngry(post.getReactionStats().getAngry() + 1); break;
        }
    }
    
    private void decrementReactionStat(Post post, String type) {
        switch (type.toLowerCase()) {
            case "like": post.getReactionStats().setLike(Math.max(0, post.getReactionStats().getLike() - 1)); break;
            case "love": post.getReactionStats().setLove(Math.max(0, post.getReactionStats().getLove() - 1)); break;
            case "haha": post.getReactionStats().setHaha(Math.max(0, post.getReactionStats().getHaha() - 1)); break;
            case "wow": post.getReactionStats().setWow(Math.max(0, post.getReactionStats().getWow() - 1)); break;
            case "sad": post.getReactionStats().setSad(Math.max(0, post.getReactionStats().getSad() - 1)); break;
            case "angry": post.getReactionStats().setAngry(Math.max(0, post.getReactionStats().getAngry() - 1)); break;
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
                    if (story.getReactions().get(i).getUser().getId().equals(user.getId())) {
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
                newStoryReaction.setUser(user);
                newStoryReaction.setCreatedAt(new Date());
                story.getReactions().add(newStoryReaction);
                story.getReactionStats().setTym(story.getReactionStats().getTym() + 1);
                action = "Đã thích story";
            }

            story.setUpdatedAt(new Date());
            Story updatedStory = storyRepository.save(story);
            return ResponseHandler.response(HttpStatus.OK, action, updatedStory);

        } catch (Exception e) {
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
            if (!story.getUser().getId().equals(user.getId())) {
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

            newComment.setUser(user);
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
            String replyText = body.get("replyText");

            System.out.println("====STEP 0=====: " + commentId);

            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();

            System.out.println("====STEP 0.1=====: " + post.getComments());
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

            newReply.setUser(user);
            newReply.setText(replyText);
            newReply.setCreatedAt(new Date());

            comment.getReplies().add(newReply);
            post.setUpdatedAt(new Date());

            newReply.setId(UUID.randomUUID().toString());
            Post updatedPost = postRepository.save(post);
            return ResponseHandler.response(HttpStatus.CREATED, "Phản hồi bình luận thành công", updatedPost);
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

            System.out.println("====STEP 0.1=====: " + post.getShare());
            if (post.getShare() == null) {
                post.setShare(new ArrayList<>());
            }

            // Kiểm tra xem user đã share chưa
            boolean hasShared = false;
            for (com.example.vibely_backend.entity.User user : post.getShare()) {
                if (user.getId().equals(userEmail)) {
                    hasShared = true;
                    break;
                }
            }

            if (!hasShared) {
                // Get the user by email
                User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

                post.getShare().add(user);
            }

            post.setShareCount(post.getShareCount() + 1);
            post.setUpdatedAt(new Date());

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
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();
            return ResponseHandler.response(HttpStatus.OK, "Lấy bài viết thành công", post);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Lấy bài viết thất bại", e.getMessage());
        }
    }

    // Chỉnh sửa bài viết
    @PutMapping("/posts/edit/{postId}")
    public ResponseEntity<?> editPost(@PathVariable String postId, @RequestBody Post updatedPost) {
        System.out.println("====ResponseEntityResponseEntityResponseEntity=====: " + postId);
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

            // Kiểm tra xem người dùng có phải là chủ bài viết không
            if (!existingPost.getUser().getId().equals(user.getId())) {
                return ResponseHandler.response(HttpStatus.FORBIDDEN, "Bạn không có quyền chỉnh sửa bài viết này");
            }

            // Cập nhật nội dung bài viết
            existingPost.setContent(updatedPost.getContent());
            existingPost.setUpdatedAt(new Date());

            Post savedPost = postRepository.save(existingPost);
            return ResponseHandler.response(HttpStatus.OK, "Chỉnh sửa bài viết thành công", savedPost);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Chỉnh sửa bài viết thất bại", e.getMessage());
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
                    if (reaction != null && reaction.getUser() != null &&
                            reaction.getUser().getId() != null &&
                            reaction.getUser().getId().equals(user.getId())) {
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
                User commentReactionUser = new User();
                commentReactionUser.setId(user.getId());
                newCommentReaction.setUser(commentReactionUser);
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
}