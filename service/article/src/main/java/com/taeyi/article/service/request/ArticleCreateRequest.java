package com.taeyi.article.service.request;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ArticleCreateRequest {
    private String title;
    private String content;
    private Long boardId;
    private Long writerId;
}