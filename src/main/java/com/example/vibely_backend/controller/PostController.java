package com.example.vibely_backend.controller;

import com.example.vibely_backend.model.Post;
import com.example.vibely_backend.model.Story;
import com.example.vibely_backend.service.CloudinaryService;
import com.example.vibely_backend.service.PostService;
import com.example.vibely_backend.service.StoryService;
import com.example.vibely_backend.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private StoryService storyService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private ResponseHandler responseHandler;

    // Tạo bài viết
    @PostMapping
    public ResponseEntity<?> createPost(@RequestParam(required = false) String content,
                                      @RequestParam(required = false) MultipartFile file) {
        try {
            String userId = "currentUserId"; // Lấy từ SecurityContext
            String mediaUrl = null;
            String mediaType = null;

            // Kiểm tra nếu có file thì upload lên Cloudinary
            if (file != null) {
                Map<String, String> uploadResult = cloudinaryService.uploadFile(file);
                if (uploadResult == null || !uploadResult.containsKey("secure_url")) {
                    return responseHandler.error("Lỗi khi tải lên tệp.", 400);
                }

                mediaUrl = uploadResult.get("secure_url");
                mediaType = file.getContentType().startsWith("video") ? "video" : "image";
            }

            // Tạo bài viết mới với các thông số ban đầu
            Post newPost = new Post();
            newPost.setUser(userId);
            newPost.setContent(content);
            newPost.setMediaUrl(mediaUrl);
            newPost.setMediaType(mediaType);
            newPost.setReactionCount(0);
            newPost.setCommentCount(0);
            newPost.setShareCount(0);
            
            Map<String, Integer> reactionStats = new HashMap<>();
            reactionStats.put("like", 0);
            reactionStats.put("love", 0);
            reactionStats.put("haha", 0);
            reactionStats.put("wow", 0);
            reactionStats.put("sad", 0);
            reactionStats.put("angry", 0);
            newPost.setReactionStats(reactionStats);

            Post savedPost = postService.save(newPost);
            return responseHandler.success("Tạo bài viết thành công", savedPost, 201);

        } catch (Exception e) {
            return responseHandler.error("Tạo bài viết thất bại: " + e.getMessage(), 500);
        }
    }

    // Tạo story
    @PostMapping("/stories")
    public ResponseEntity<?> createStory(@RequestParam MultipartFile file) {
        try {
            String userId = "currentUserId"; // Lấy từ SecurityContext
            
            if (file == null) {
                return responseHandler.error("Cần tải file lên để tạo story", 400);
            }

            boolean isVideo = file.getContentType().startsWith("video");
            boolean isImage = file.getContentType().startsWith("image");

            if (!isVideo && !isImage) {
                return responseHandler.error("Chỉ hỗ trợ ảnh hoặc video", 400);
            }

            Map<String, String> uploadResult = cloudinaryService.uploadFile(file);
            if (uploadResult == null || !uploadResult.containsKey("secure_url")) {
                return responseHandler.error("Lỗi khi tải file lên", 500);
            }

            Story newStory = new Story();
            newStory.setUser(userId);
            newStory.setMediaUrl(uploadResult.get("secure_url"));
            newStory.setMediaType(isVideo ? "video" : "image");

            Story savedStory = storyService.save(newStory);
            return responseHandler.success("Tạo story thành công", savedStory, 201);

        } catch (Exception e) {
            return responseHandler.error("Tạo story thất bại: " + e.getMessage(), 500);
        }
    }

    // Lấy tất cả bài viết
    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        try {
            return responseHandler.success("Lấy tất cả bài viết thành công", 
                postService.findAllWithDetails(), 200);
        } catch (Exception e) {
            return responseHandler.error("Lấy tất cả bài viết thất bại: " + e.getMessage(), 500);
        }
    }

    // Lấy tất cả story
    @GetMapping("/stories")
    public ResponseEntity<?> getAllStories() {
        try {
            return responseHandler.success("Lấy tất cả story thành công", 
                storyService.findAllWithDetails(), 200);
        } catch (Exception e) {
            return responseHandler.error("Lấy tất cả story thất bại: " + e.getMessage(), 500);
        }
    }

    // Lấy bài viết theo ID người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostByUserId(@PathVariable String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return responseHandler.error("Yêu cầu mã người dùng để lấy bài viết", 400);
            }
            return responseHandler.success("Lấy bài viết theo ID người dùng thành công", 
                postService.findByUserId(userId), 200);
        } catch (Exception e) {
            return responseHandler.error("Lấy bài viết theo ID người dùng thất bại: " + e.getMessage(), 500);
        }
    }

    // React bài viết
    @PostMapping("/{postId}/react")
    public ResponseEntity<?> reactPost(@PathVariable String postId, 
                                     @RequestParam String type) {
        try {
            String userId = "currentUserId"; // Lấy từ SecurityContext
            return responseHandler.success("Thêm reaction thành công", 
                postService.reactPost(postId, userId, type), 200);
        } catch (Exception e) {
            return responseHandler.error("Thích bài viết thất bại: " + e.getMessage(), 500);
        }
    }

    // Thả tym story
    @PostMapping("/stories/{storyId}/react")
    public ResponseEntity<?> reactStory(@PathVariable String storyId) {
        try {
            String userId = "currentUserId"; // Lấy từ SecurityContext
            return responseHandler.success("Thả tym story thành công", 
                storyService.reactStory(storyId, userId), 200);
        } catch (Exception e) {
            return responseHandler.error("Thả tym story thất bại: " + e.getMessage(), 500);
        }
    }

    // Bình luận bài viết
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addCommentToPost(@PathVariable String postId, 
                                            @RequestParam String text) {
        try {
            String userId = "currentUserId"; // Lấy từ SecurityContext
            return responseHandler.success("Bình luận bài viết thành công", 
                postService.addComment(postId, userId, text), 201);
        } catch (Exception e) {
            return responseHandler.error("Bình luận bài viết thất bại: " + e.getMessage(), 500);
        }
    }

    // Phản hồi bình luận
    @PostMapping("/{postId}/comments/{commentId}/replies")
    public ResponseEntity<?> addReplyToPost(@PathVariable String postId,
                                          @PathVariable String commentId,
                                          @RequestParam String replyText) {
        try {
            String userId = "currentUserId"; // Lấy từ SecurityContext
            return responseHandler.success("Phản hồi bình luận thành công", 
                postService.addReply(postId, commentId, userId, replyText), 201);
        } catch (Exception e) {
            return responseHandler.error("Phản hồi bình luận thất bại: " + e.getMessage(), 500);
        }
    }

    // Chia sẻ bài viết
    @PostMapping("/{postId}/share")
    public ResponseEntity<?> sharePost(@PathVariable String postId) {
        try {
            String userId = "currentUserId"; // Lấy từ SecurityContext
            return responseHandler.success("Chia sẻ bài viết thành công", 
                postService.sharePost(postId, userId), 200);
        } catch (Exception e) {
            return responseHandler.error("Chia sẻ bài viết thất bại: " + e.getMessage(), 500);
        }
    }

    // Xóa bài viết
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable String postId) {
        try {
            Post deletedPost = postService.deletePost(postId);
            return responseHandler.success("Xóa bài viết thành công", deletedPost, 200);
        } catch (Exception e) {
            return responseHandler.error("Xóa bài viết thất bại: " + e.getMessage(), 500);
        }
    }

    // Xóa bình luận
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String postId,
                                         @PathVariable String commentId) {
        try {
            Post updatedPost = postService.deleteComment(postId, commentId);
            return responseHandler.success("Xóa bình luận thành công", updatedPost, 200);
        } catch (Exception e) {
            return responseHandler.error("Xóa bình luận thất bại: " + e.getMessage(), 500);
        }
    }

    // Xóa phản hồi
    @DeleteMapping("/{postId}/comments/{commentId}/replies/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable String postId,
                                       @PathVariable String commentId,
                                       @PathVariable String replyId) {
        try {
            Post updatedPost = postService.deleteReply(postId, commentId, replyId);
            return responseHandler.success("Xóa phản hồi thành công", updatedPost, 200);
        } catch (Exception e) {
            return responseHandler.error("Xóa phản hồi thất bại: " + e.getMessage(), 500);
        }
    }

    // Lấy một bài viết
    @GetMapping("/{postId}")
    public ResponseEntity<?> getSinglePost(@PathVariable String postId) {
        try {
            Post post = postService.findByIdWithDetails(postId);
            if (post == null) {
                return responseHandler.error("Không tìm thấy bài viết", 404);
            }
            return responseHandler.success("Lấy bài viết thành công", post, 200);
        } catch (Exception e) {
            return responseHandler.error("Lấy bài viết thất bại: " + e.getMessage(), 500);
        }
    }

    // Thích bình luận
    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable String postId,
                                       @PathVariable String commentId) {
        try {
            String userId = "currentUserId"; // Lấy từ SecurityContext
            Post updatedPost = postService.likeComment(postId, commentId, userId);
            return responseHandler.success("Thích bình luận thành công", updatedPost, 201);
        } catch (Exception e) {
            return responseHandler.error("Thích bình luận thất bại: " + e.getMessage(), 500);
        }
    }
}