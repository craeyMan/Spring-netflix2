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

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String keyword) {
        List<Post> result = postRepository.findByTitleContainingIgnoreCase(keyword);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestBody Post post,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Post newPost = Post.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .author(userDetails.getUsername())
                .createdAt(LocalDateTime.now().withNano(0))
                .isSecret(post.isSecret())
                .build();

        return ResponseEntity.ok(postRepository.save(newPost));
    }

    @GetMapping
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.ok(postRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
        }

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String currentUser = userDetails.getUsername();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (post.isSecret()) {
            if (!post.getAuthor().equals(currentUser) && !isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("비밀글 접근 권한이 없습니다.");
            }
        }

        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody Post updatedPost,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return postRepository.findById(id)
                .map(post -> {
                    if (!post.getAuthor().equals(userDetails.getUsername())) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("작성자만 수정할 수 있습니다.");
                    }

                    post.setTitle(updatedPost.getTitle());
                    post.setContent(updatedPost.getContent());
                    post.setSecret(updatedPost.isSecret());
                    return ResponseEntity.ok(postRepository.save(post));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
