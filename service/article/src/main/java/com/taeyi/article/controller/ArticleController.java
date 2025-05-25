package com.taeyi.article.controller;

import com.taeyi.article.service.ArticleService;
import com.taeyi.article.service.request.ArticleCreateRequest;
import com.taeyi.article.service.request.ArticleUpdateRequest;
import com.taeyi.article.service.response.ArticlePageResponse;
import com.taeyi.article.service.response.ArticleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/v1/articles/{articleId}")
    public ArticleResponse getArticle(@PathVariable Long articleId) {
        return articleService.read(articleId);
    }

    @GetMapping("/v1/articles")
    public ArticlePageResponse readAll(@RequestParam Long boardId, @RequestParam Long page, @RequestParam Long pageSize) {
        return articleService.readAll(boardId, page, pageSize);
    }

    @PostMapping("/v1/articles")
    public ArticleResponse createArticle(@RequestBody ArticleCreateRequest request) {
        return articleService.create(request);
    }

    @PatchMapping("/v1/articles/{articleId}")
    public ArticleResponse updateArticle(@PathVariable Long articleId, @RequestBody ArticleUpdateRequest request) {
        return articleService.update(articleId, request);
    }

    @DeleteMapping("/v1/articles/{articleId}")
    public void deleteArticle(@PathVariable Long articleId) {
        articleService.delete(articleId);
    }
}