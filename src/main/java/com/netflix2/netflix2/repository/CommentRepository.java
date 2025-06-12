package com.netflix2.netflix2.repository;

import com.netflix2.netflix2.entity.Comment;
import com.netflix2.netflix2.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    @Query("SELECT c FROM Comment c WHERE c.post = :post AND (c.visibleTo = :user OR c.author = :user)")
    List<Comment> findVisibleComments(@Param("post") Post post, @Param("user") String user);
}
