package com.netflix2.netflix2.service;

import com.netflix2.netflix2.entity.Comment;
import com.netflix2.netflix2.entity.Post;
import com.netflix2.netflix2.repository.CommentRepository;
import com.netflix2.netflix2.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<Comment> getVisibleComments(Long postId, String currentUser) {
        Post post = postRepository.findById(postId).orElseThrow();
        return commentRepository.findVisibleComments(post, currentUser);
    }

    public Comment createCommentWithDetails(Post post,
                                             String author,
                                             String content,
                                             String visibleTo,
                                             Long parentId,
                                             String role) {
        Comment.CommentBuilder builder = Comment.builder()
                .post(post)
                .author(author)
                .content(content)
                .visibleTo(visibleTo)
                .role(role);

        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId).orElseThrow();
            builder.parent(parent);
        }

        return commentRepository.save(builder.build());
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
