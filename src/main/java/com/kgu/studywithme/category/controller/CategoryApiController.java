package com.kgu.studywithme.category.controller;

import com.kgu.studywithme.category.service.CategoryService;
import com.kgu.studywithme.category.service.dto.response.CategoryResponse;
import com.kgu.studywithme.global.dto.SimpleResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryApiController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<SimpleResponseWrapper<List<CategoryResponse>>> findALl() {
        return ResponseEntity.ok(new SimpleResponseWrapper<>(categoryService.findAll()));
    }
}
