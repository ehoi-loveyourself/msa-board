package com.taeyi.article.api;

import com.taeyi.article.service.response.ArticlePageResponse;
import com.taeyi.article.service.response.ArticleResponse;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

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
    void readAllInfiniteScroll() {
        List<ArticleResponse> responses = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("firstPage");
        for (ArticleResponse response : responses) {
            System.out.println("articleId = " + response.getArticleId());
        }

        Long lastArticleId = responses.getLast().getArticleId();
        List<ArticleResponse> responses2 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });
        System.out.println("secondPage");
        for (ArticleResponse response : responses2) {
            System.out.println("articleId = " + response.getArticleId());
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