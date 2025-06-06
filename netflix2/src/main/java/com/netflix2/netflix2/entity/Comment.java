package com.netflix2.netflix2.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    @Column(name = "visible_to")
    private String visibleTo; // ✅ 누가 이 댓글을 볼 수 있는지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;   // ✅ 대댓글용 (null이면 일반 댓글)

    @Column(name = "role")
    private String role;      // ✅ USER 또는 ADMIN

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
