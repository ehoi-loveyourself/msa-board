package com.taeyi.article.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // 외부에서는 기본 생성자로 객체를 만들 수 없음 -> 불변객체를 만들고 싶을 때
public final class PageLimitCalculator {

    // 조회할 페이지, 페이지당 게시물 수, 이동가능한 페이지 수를 파라미터로 넣었을 때
    // 로드해야하는 게시물 수 계산
    public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageCount) {
        return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1;
    }
}