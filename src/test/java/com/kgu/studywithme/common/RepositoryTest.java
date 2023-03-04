package com.kgu.studywithme.common;

import com.kgu.studywithme.global.config.QueryDslConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslConfiguration.class)
public class RepositoryTest {
}
