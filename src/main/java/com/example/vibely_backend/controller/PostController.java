package com.example.vibely_backend.controller;

import com.example.vibely_backend.entity.Post;
import com.example.vibely_backend.entity.Story;
import com.example.vibely_backend.entity.User;
import com.example.vibely_backend.repository.PostRepository;
import com.example.vibely_backend.repository.StoryRepository;
import com.example.vibely_backend.service.CloudinaryService;
import com.example.vibely_backend.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // Tạo bài viết
    @PostMapping
    public ResponseEntity<?> createPost(@RequestParam(required = false) String content,
            @RequestParam(required = false) MultipartFile file) {
        System.out.println("createPostcreatePostcreatePostcreatePostcreatePostcreatePost: " + content);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + authentication);
            String userId = authentication.getName();

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

            // Tạo bài viết mới với các thông số ban đầu
            Post newPost = new Post();
            User user = new User();
            user.setId(userId);
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
    public ResponseEntity<?> createStory(MultipartFile file) {
        System.out.println("createStorycreateStorycreateStorycreateStorycreateStorycreateStory: " + file);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

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

            User storyUser = new User();
            storyUser.setId(userId);
            newStory.setUser(storyUser);
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

    // Lấy tất cả bài viết
    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        try {
            List<Post> posts = postRepository.findAll();
            return ResponseHandler.response(HttpStatus.OK, "Lấy tất cả bài viết thành công", posts);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Lấy tất cả bài viết thất bại",
                    e.getMessage());
        }
    }

    // Lấy tất cả story
    @GetMapping("/stories")
    public ResponseEntity<?> getAllStories() {
        try {
            List<Story> stories = storyRepository.findAll();
            return ResponseHandler.response(HttpStatus.OK, "Lấy tất cả story thành công", stories);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Lấy tất cả story thất bại",
                    e.getMessage());
        }
    }

    // Lấy bài viết theo ID người dùng
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getPostByUserId(@PathVariable String id) {
        try {
            if (id == null || id.isEmpty()) {
                return ResponseHandler.response(HttpStatus.BAD_REQUEST, "Yêu cầu mã người dùng để lấy bài viết");
            }
            List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(id);
            return ResponseHandler.response(HttpStatus.OK, "Lấy bài viết theo ID người dùng thành công", posts);
        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Lấy bài viết theo ID người dùng thất bại", e.getMessage());
        }
    }

    // React bài viết
    @PostMapping("/{postId}/react")
    public ResponseEntity<?> reactPost(@PathVariable String postId, @RequestBody Map<String, String> body) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            String type = body.get("type");
            Optional<Post> postOpt = postRepository.findById(postId);
            if (postOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy bài viết");
            }

            Post post = postOpt.get();
            System.out.println("====STEP 0=====: " + post.getReactionStats());
            if (post.getReactionStats() == null) {
                post.setReactionStats(new Post.ReactionStats());
            }
            System.out.println("====STEP 0.1=====: " + post.getReactionStats());

            // Tìm reaction của user nếu có
            int existingReactionIndex = findExistingReactionIndex(post, userId);
            System.out.println("====STEP 0.2=====: " + existingReactionIndex);

            // This variable is used in the response message
            String action = processReaction(post, existingReactionIndex, type, userId);
            System.out.println("====STEP 0.3=====: " + action);
            post.setUpdatedAt(new Date());
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

    private String processReaction(Post post, int existingReactionIndex, String type, String userId) {
        String action = "";
        if (existingReactionIndex != -1) {
            // Nếu user đã phản ứng => bỏ phản ứng
            post.getReactions().remove(existingReactionIndex);
            action = "Đã bỏ thích bài viết";
        } else {
            // Nếu chưa phản ứng => thêm phản ứng
            if (post.getReactions() == null) {
                post.setReactions(new java.util.ArrayList<>());
            }

            System.out.println("====STEP 0.1=====: " + post.getReactions());
            Post.Reaction newReaction = new Post.Reaction();

            User reactionUser = new User();
            reactionUser.setId(userId);
            newReaction.setUser(reactionUser);
            newReaction.setCreatedAt(new Date());
            newReaction.setType(type);
            if (post.getReactions() != null) {
                post.getReactions().add(newReaction);
            } else {
                List<Post.Reaction> reactions = new java.util.ArrayList<>();
                reactions.add(newReaction);
                post.setReactions(reactions);
            }
            action = "Đã thích bài viết";
        }
        return action;
    }

    private int findExistingReactionIndex(Post post, String userId) {
        int existingReactionIndex = -1;
        System.out.println("====STEP 0.4=====: " + post.getReactions());
        if (post.getReactions() != null) {
            for (int i = 0; i < post.getReactions().size(); i++) {
                if (post.getReactions().get(i).getUser().getId().equals(userId)) {
                    existingReactionIndex = i;
                    break;
                }
            }
        }
        System.out.println("====STEP 0.5=====: " + existingReactionIndex);
        return existingReactionIndex;
    }

    // Thả tym story
    @PostMapping("/stories/{storyId}/react")
    public ResponseEntity<?> reactStory(@PathVariable String storyId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

            Optional<Story> storyOpt = storyRepository.findById(storyId);
            if (storyOpt.isEmpty()) {
                return ResponseHandler.response(HttpStatus.NOT_FOUND, "Không tìm thấy story");
            }

            Story story = storyOpt.get();
            System.out.println("====STEP 0=====: " + story.getReactionStats());
            // Tìm reaction của user nếu có

            if (story.getReactionStats() == null) {
                story.setReactionStats(new Story.ReactionStats());
            }
            System.out.println("====STEP 0.1=====: " + story.getReactionStats());

            int existingReactionIndex = -1;
            if (story.getReactions() != null) {
                for (int i = 0; i < story.getReactions().size(); i++) {
                    if (story.getReactions().get(i).getUser().getId().equals(userId)) {
                        existingReactionIndex = i;
                        break;
                    }
                }
            }

            // This variable is used in the response message at the end of the method
            String action = "";

            if (existingReactionIndex != -1) {
                // Nếu user đã tym => bỏ tym
                story.getReactions().remove(existingReactionIndex);
                story.getReactionStats().setTym(Math.max(0, story.getReactionStats().getTym() - 1));
                action = "Đã thích story";
            } else {
                // Nếu chưa tym => thêm tym
                Story.Reaction newStoryReaction = new Story.Reaction();
                User storyReactionUser = new User();
                storyReactionUser.setId(userId);
                newStoryReaction.setUser(storyReactionUser);
                newStoryReaction.setCreatedAt(new Date());
                if (story.getReactions() == null) {
                    story.setReactions(new ArrayList<>());
                }
                story.getReactions().add(newStoryReaction);
                story.getReactionStats().setTym(story.getReactionStats().getTym() + 1);
                action = "Bỏ thích story";
            }

            story.setUpdatedAt(new Date());
            Story updatedStory = storyRepository.save(story);
            return ResponseHandler.response(HttpStatus.OK, action, updatedStory);

        } catch (Exception e) {
            return ResponseHandler.response(HttpStatus.INTERNAL_SERVER_ERROR, "Thả tym story thất bại", e.getMessage());
        }
    }

    // Bình luận bài viết
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addCommentToPost(@PathVariable String postId, @RequestBody Map<String, String> body) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
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
            User commentUser = new User();
            commentUser.setId(userId);
            newComment.setUser(commentUser);
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
    @PostMapping("/{postId}/comments/reply")
    public ResponseEntity<?> addReplyToPost(@PathVariable String postId, @RequestBody Map<String, String> body) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
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
            User replyUser = new User();
            replyUser.setId(userId);
            newReply.setUser(replyUser);
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
    @PostMapping("/{postId}/share")
    public ResponseEntity<?> sharePost(@PathVariable String postId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();

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
                if (user.getId().equals(userId)) {
                    hasShared = true;
                    break;
                }
            }

            if (!hasShared) {
                User shareUser = new User();
                shareUser.setId(userId);
                post.getShare().add(shareUser);
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
    @DeleteMapping("/{postId}/delete")
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
    @DeleteMapping("/{postId}/comments/{commentId}/delete")
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
    @DeleteMapping("/{postId}/comments/{commentId}/replies/{replyId}/delete")
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
    @GetMapping("/{postId}")
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

    // Thích bình luận
    @PostMapping("/{postId}/comments/{commentId}/react")
    public ResponseEntity<?> reactComment(@PathVariable String postId, @PathVariable String commentId,
            @RequestBody Map<String, String> body) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication.getName();
            String type = body.get("type");

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
                            reaction.getUser().getId().equals(userId)) {
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
                commentReactionUser.setId(userId);
                newCommentReaction.setUser(commentReactionUser);
                newCommentReaction.setType(type);
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