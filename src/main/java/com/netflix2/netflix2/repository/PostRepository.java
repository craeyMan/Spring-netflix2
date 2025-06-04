package com.netflix2.netflix2.repository;

import com.netflix2.netflix2.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findByTitleContainingIgnoreCase(String keyword);
}
