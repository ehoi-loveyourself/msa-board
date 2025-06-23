package com.taeyi.comment.service;

import com.taeyi.comment.entity.Comment;
import com.taeyi.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Junit5에서 Mockito를 사용할 수 있도록 확장시켜주는 애너테이션
 * @Mock, @InjectMocks 등의 애너테이션을 사용할 수 있게 해줌
 * 즉, 테스트 클래스에서 Mockito의 기능을 활성화해주는 설정
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    /**
     * commentService 객체를 실제로 생성
     * 해당 클래스가 의존하는 필드(여기서는 commentRepository)에 대해 @Mock으로 선언된 객체들을 자동으로 주입 = 의존성 주입
     * 예: CommentService 안에 CommentRepository가 있다면, 아래의 @Mock 객체가 주입
     */
    @InjectMocks
    CommentService commentService;

    /**
     * CommentRepository는 테스트 중 실제 DB에 접근하지 않도록, 가짜(mock) 객체로 생성
     * commentService가 이 commentRepository를 사용하더라도, 동작은 Mockito가 정의한 가짜 동작을 따릅
     */
    @Mock
    CommentRepository commentRepository;

    @Test
    @DisplayName("삭제할 댓글이 자식이 있으면, 삭제 표시만 한다.")
    void deleteShouldMarkDeletedIfHasChildren() {
        // given: 삭제할 댓글이 자식이 있도록 세팅
        Long articleId = 1L;
        Long commentId = 2L;
        Comment comment = createComment(articleId, commentId);
        // 아래 when 에서 delete 메서드를 호출하면
        // 자식이 있는 경우에는 repository 메서드 중 findById, countChildrenIncludingMe 를 호출하므로
        // 그에 대한 결과 또한 세팅해줘야 한다.
        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        given(commentRepository.countChildrenIncludingMe(articleId, commentId, 2L))
                .willReturn(2L);

        // when: 서비스에서 삭제 메서드를 호출했을 때
        commentService.delete(commentId);

        // then
        verify(comment).delete();
    }

    @Test
    @DisplayName("하위 댓글(주체)이 삭제되고, 삭제되지 않은 부모면, 하위댓글만 삭제한다.")
    void deleteShouldDeleteChildOnlyIfNotDeletedParent() {
        // given: 삭제할 댓글이 자식이 있도록 세팅
        Long articleId = 1L;
        Long commentId = 2L;
        Long parentCommentId = 1L;

        Comment comment = createComment(articleId, commentId, parentCommentId);
        given(comment.isRoot()).willReturn(false);

        Comment parentComment = mock(Comment.class);
        given(parentComment.getDeleted()).willReturn(false);

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        given(commentRepository.countChildrenIncludingMe(articleId, commentId, 2L))
                .willReturn(1L);

        given(commentRepository.findById(parentCommentId))
                .willReturn(Optional.of(parentComment));

        // when: 서비스에서 삭제 메서드를 호출했을 때
        commentService.delete(commentId);

        // then
        verify(commentRepository).delete(comment);
        verify(commentRepository, never()).delete(parentComment);
    }

    @Test
    @DisplayName("하위 댓글(주체)이 삭제되고, 부모도 삭제됐으면, 재귀적으로 부모도 삭제한다.")
    void deleteShouldDeleteAllRecursivelyIfDeletedParent() {
        // given: 삭제할 댓글이 자식이 있도록 세팅
        Long articleId = 1L;
        Long commentId = 2L;
        Long parentCommentId = 1L;

        Comment comment = createComment(articleId, commentId, parentCommentId);
        given(comment.isRoot()).willReturn(false);

        Comment parentComment = createComment(articleId, parentCommentId);
        given(parentComment.isRoot()).willReturn(true);
        given(parentComment.getDeleted()).willReturn(true);

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        given(commentRepository.countChildrenIncludingMe(articleId, commentId, 2L))
                .willReturn(1L);

        given(commentRepository.findById(parentCommentId))
                .willReturn(Optional.of(parentComment));
        given(commentRepository.countChildrenIncludingMe(articleId, parentCommentId, 2L))
                .willReturn(1L);

        // when: 서비스에서 삭제 메서드를 호출했을 때
        commentService.delete(commentId);

        // then
        verify(commentRepository).delete(comment);
        verify(commentRepository).delete(parentComment);
    }

    private Comment createComment(Long articleId, Long commentId) {
        Comment comment = mock(Comment.class);
        given(comment.getArticleId()).willReturn(articleId);
        given(comment.getCommentId()).willReturn(commentId);

        return comment;
    }

    private Comment createComment(Long articleId, Long commentId, Long parentCommentId) {
        Comment comment = createComment(articleId, commentId);
        given(comment.getParentCommentId()).willReturn(parentCommentId);

        return comment;
    }
}