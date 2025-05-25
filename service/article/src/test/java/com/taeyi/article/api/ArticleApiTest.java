package com.taeyi.article.api;

import com.taeyi.article.service.response.ArticlePageResponse;
import com.taeyi.article.service.response.ArticleResponse;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void createTest() {
        ArticleResponse response = create(ArticleCreateRequest.builder()
                .title("hi")
                .content("my content")
                .boardId(1L)
                .writerId(1L)
                .build());
        System.out.println("response = " + response);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readTest() {
        ArticleResponse response = read(174110562816880640L);
        System.out.println("response = " + response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readAllTest() {
        ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&page=50000&pageSize=30")
                .retrieve()
                .body(ArticlePageResponse.class);

        System.out.println("response.getArticleCount() = " + response.getArticleCount());
        for (ArticleResponse article : response.getArticles()) {
            System.out.println("articleId = " + article.getArticleId());
        }
    }

    @Test
    void update() {
        update(174110562816880640L);
        ArticleResponse response = read(174110562816880640L);
        System.out.println("response = " + response);
    }

    void update(Long articleId) {
        restClient.patch()
                .uri("/v1/articles/{articleId}", articleId)
                .body(ArticleUpdateRequest.builder()
                        .title("수정한 제목")
                        .content("수정한 내용")
                        .build())
                .retrieve();
    }

    @Test
    void deleteTest() {
        delete(174110562816880640L);
    }

    void delete(Long articleId) {
        restClient.delete()
                .uri("/v1//articles/{articleId}", articleId)
                .retrieve();
    }

    @Getter
    @Builder
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long boardId;
        private Long writerId;
    }

    @Getter
    @Builder
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }
}