package com.netflix2.netflix2.controller;

import com.netflix2.netflix2.entity.Post;
import com.netflix2.netflix2.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;

    // 🔍 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String keyword) {
        List<Post> result = postRepository.findByTitleContainingIgnoreCase(keyword);
        return ResponseEntity.ok(result);
    }

    // 📝 게시글 등록
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestBody Post post,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        post.setCreatedAt(LocalDateTime.now().withNano(0));
        post.setAuthor(userDetails.getUsername()); // ✅ 작성자 저장
        return ResponseEntity.ok(postRepository.save(post));
    }

    // 📄 전체 게시글 조회
    @GetMapping
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.ok(postRepository.findAll());
    }

    // 📌 게시글 1개 조회
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✏️ 게시글 수정 (작성자만 가능)
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody Post updatedPost,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return postRepository.findById(id)
                .map(post -> {
                    // ✅ 디버깅 로그 추가
                    System.out.println("🔒 로그인 유저: " + userDetails.getUsername());
                    System.out.println("📝 게시글 작성자: " + post.getAuthor());

                    if (!post.getAuthor().equals(userDetails.getUsername())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("작성자만 수정할 수 있습니다.");
                    }

                    post.setTitle(updatedPost.getTitle());
                    post.setContent(updatedPost.getContent());
                    return ResponseEntity.ok(postRepository.save(post));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 🗑️ 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
