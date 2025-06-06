package com.netflix2.netflix2.controller;

import com.netflix2.netflix2.entity.Comment;
import com.netflix2.netflix2.entity.Post;
import com.netflix2.netflix2.repository.PostRepository;
import com.netflix2.netflix2.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostRepository postRepository;

    // ✅ 댓글 조회 (비밀글이면 관리자/작성자만 허용)
    @GetMapping("/{postId}")
    public List<Comment> getComments(@PathVariable Long postId,
                                     @AuthenticationPrincipal UserDetails user) {
        Post post = postRepository.findById(postId).orElseThrow();
        String currentUser = user.getUsername();
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (post.isSecret()) {
            if (!isAdmin && !currentUser.equals(post.getAuthor())) {
                return List.of(); // ❌ 열람 권한 없음
            }
        }

        return commentService.getVisibleComments(postId, currentUser);
    }

    // ✅ 댓글 작성
    @PostMapping
    public Comment createComment(@RequestBody CommentRequest request,
                                 @AuthenticationPrincipal UserDetails user) {
        Post post = postRepository.findById(request.postId()).orElseThrow();
        String currentUser = user.getUsername();
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (post.isSecret()) {
            if (!isAdmin && !currentUser.equals(post.getAuthor())) {
                throw new RuntimeException("비밀글에는 댓글을 작성할 수 없습니다.");
            }
        }

        String role = isAdmin ? "ADMIN" : "USER";
        String visibleTo = isAdmin ? post.getAuthor() : "admin";

        return commentService.createCommentWithDetails(
                post,
                currentUser,
                request.content(),
                visibleTo,
                request.parentId(),
                role
        );
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    public record CommentRequest(
            Long postId,
            String content,
            Long parentId
    ) {}
}
