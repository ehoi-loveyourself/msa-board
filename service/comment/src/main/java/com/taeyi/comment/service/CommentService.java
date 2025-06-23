package com.taeyi.comment.service;

import com.taeyi.comment.entity.Comment;
import com.taeyi.comment.repository.CommentRepository;
import com.taeyi.comment.service.request.CommentCreateRequest;
import com.taeyi.comment.service.response.CommentResponse;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void delete(Long commentId) {
        commentRepository.findById(commentId)
                .filter(not(Comment::getDeleted)) // 삭제 되지 않은 것만 삭제 표시 가능
                .ifPresent(comment -> {
                    if (hasChildren(comment)) {
                        // 자식이 있으면 삭제 표시만 해야함
                        comment.delete();
                    } else {
                        // 삭제할 수 있음
                        delete(comment);
                    }
                });
    }

    private boolean hasChildren(Comment comment) {
        return commentRepository.countChildrenIncludingMe(comment.getArticleId(), comment.getCommentId(), 2L) == 2;
        // 1개는 가장 상위 댓글인 나 자신이므로 2개 이상이면 무조건 하위 댓글이 있는 것이다.
        // 그래서 limit을 2로 주고 2개까지만 조회하도록 함. 그 이상 조회를 해봤자 의미가 없으므로!
    }

    private void delete(Comment comment) {
        // 일단 삭제
        commentRepository.delete(comment);
        // 내가 가장 상위 댓글이 아니면 내가 삭제됨으로써 상위 댓글을 삭제할 수 있는지 판단하여 재귀적으로 삭제해야함
        if (!comment.isRoot()) {
            commentRepository.findById(comment.getParentCommentId()) // 상위 댓글 찾기
                    .filter(Comment::getDeleted) // 조건 1. 삭제 된 것만
                    .filter(not(this::hasChildren)) // 조건 2. 하위 댓글이 다 삭제된 것만
                    .ifPresent(this::delete);
        }
    }
}
