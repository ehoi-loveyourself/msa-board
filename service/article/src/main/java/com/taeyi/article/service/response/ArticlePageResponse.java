package com.taeyi.article.service.response;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class ArticlePageResponse {
    private List<ArticleResponse> articles;
    private Long articleCount;

    public static ArticlePageResponse of(List<ArticleResponse> articles, Long articleCount) {
        ArticlePageResponse articlePageResponse = new ArticlePageResponse();
        articlePageResponse.articles = articles;
        articlePageResponse.articleCount = articleCount;

        return articlePageResponse;
    }
}