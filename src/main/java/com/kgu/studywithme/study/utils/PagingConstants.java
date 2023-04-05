package com.kgu.studywithme.study.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface PagingConstants {
    int SLICE_PER_PAGE = 8;
    String SORT_DATE = "date";
    String SORT_FAVORITE = "favorite";
    String SORT_REVIEW = "review";

    static Pageable getDefaultPageRequest(int page) {
        return PageRequest.of(page, SLICE_PER_PAGE);
    }
}
