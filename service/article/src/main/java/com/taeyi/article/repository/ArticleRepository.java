package com.taeyi.article.repository;

import com.taeyi.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /* Pageable을 사용하면 우리가 사용하고자 하는 최적화된 쿼리가 만들어지지 않으므로 직접 네이티브 쿼리를 사용한다. */
    @Query(
            value = """
                select article.article_id, article.title, article.content, article.board_id, article.writer_id, article.created_at, article.modified_at
                from (
                    select article_id 
                      from article
                    where board_id = :boardId
                    order by article_id desc
                    limit :limit offset :offset
                ) t 
                left join article
                on t.article_id = article.article_id
                """,
            nativeQuery = true
    )
    List<Article> findAll(
      @Param("boardId") Long boardId,
      @Param("offset") Long offset,
      @Param("limit") Long limit
    );
    /* 여기서 왜 left join 을 하는지 궁금했는데 inner join 과 값이 같다고 함.
    왜냐하면 어차피 최적화를 위해서 필요한 article_id만 서브쿼리로 뽑아온 것이기 때문에
    left join의 의미가 따로 있지 않음
     */

    @Query(
            value = """
                select count(*)
                from (
                    select article_id 
                      from article
                    where board_id = :boardId
                    limit :limit
                ) t
                """,
            nativeQuery = true
    )
    Long count(
            @Param("boardId") Long boardId,
            @Param("limit") Long limit
    );
}
