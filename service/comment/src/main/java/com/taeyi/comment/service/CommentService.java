package com.taeyi.comment.service;

import com.taeyi.comment.entity.Comment;
import com.taeyi.comment.repository.CommentRepository;
import com.taeyi.comment.service.request.CommentCreateRequest;
import com.taeyi.comment.service.response.CommentResponse;
import jakarta.transaction.Transactional;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

import static java.util.function.Predicate.not;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final Snowflake snowflake = new Snowflake();
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse create(CommentCreateRequest request) {
        Comment parent = findParent(request.getParentCommentId());

        Comment saved = commentRepository.save(
                Comment.create(
                        snowflake.nextId(),
                        request.getContent(),
                        request.getArticleId(),
                        parent == null ? null : parent.getCommentId(),
                        request.getWriterId()
                )
        );

        return CommentResponse.from(saved);
    }

    private Comment findParent(Long parentCommentId) {
        if (parentCommentId == null) {
            return null;
        }

        return commentRepository.findById(parentCommentId) // 작성하려고 하는 댓글의 상위 댓글이 있는가?
                .filter(not(Comment::getDeleted)) // 필터 1) 삭제 안된 것만
                .filter(Comment::isRoot) // 핉터 2) 최상위 댓글
                .orElseThrow();
    }

    public CommentResponse read(Long commentId) {
        return CommentResponse.from(
                commentRepository.findById(commentId).orElseThrow()
        );
    }

    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow();


    }
}
