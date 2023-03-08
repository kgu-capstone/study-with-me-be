package com.kgu.studywithme.common.config;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;

public class MockMvcConfiguration implements MockMvcBuilderCustomizer {
    @Override
    public void customize(ConfigurableMockMvcBuilder<?> builder) {
        builder.alwaysDo(result -> result.getResponse().setCharacterEncoding("UTF-8"));
    }
}
