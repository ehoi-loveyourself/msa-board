package com.taeyi.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Objects;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    private Long commentId;
    private String content;
    private Long articleId; // shard key
    private Long parentCommentId;
    private Long writerId;
    private Boolean deleted;
    private LocalDateTime createdAt;

    public static Comment create(Long commentId, String content, Long articleId, Long parentCommentId, Long writerId) {
        Comment comment = new Comment();
        comment.commentId = commentId;
        comment.content = content;
        comment.articleId = articleId;
        comment.parentCommentId = parentCommentId == null ? commentId : parentCommentId;
        comment.writerId = writerId;
        comment.deleted = false;
        comment.createdAt = LocalDateTime.now();

        return comment;
    }

    public boolean isRoot() {
//        return parentCommentId.longValue() == commentId; // 강의 버전
        return Objects.equals(this.parentCommentId, this.commentId); // null-safe를 위해 내가 작성한 버전
    }

    public void delete() {
        this.deleted = true;
    }
}
