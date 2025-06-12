package com.netflix2.netflix2.controller;

import com.netflix2.netflix2.dto.LikeDTO;
import com.netflix2.netflix2.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/top10")
    public List<LikeDTO> getTop10LikedMovies() {
        return likeService.getTop10LikedMovies();
    }

    @PostMapping
    public ResponseEntity<?> addLike(@RequestBody Map<String, String> request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
        }

        String userId = authentication.getName();
        String movieId = request.get("movieId");
        likeService.addLike(userId, movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<?> removeLike(@PathVariable String movieId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
        }

        String userId = authentication.getName();
        likeService.removeLike(userId, movieId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<Map<String, Boolean>> isLiked(@PathVariable String movieId, Authentication authentication) {
        String userId = authentication.getName();
        boolean liked = likeService.isLiked(userId, movieId);
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
