package com.dnd.runus.config;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Import({
    JooqAutoConfiguration.class,
    JooqProperties.class,
})
@TestConfiguration
@RequiredArgsConstructor
public class TestJooqConfig {
    private final DataSource dataSource;
    private final JooqProperties jooqProperties;

    @Bean
    DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    DSLContext dsl() {
        return DSL.using(connectionProvider(), jooqProperties.determineSqlDialect(dataSource));
    }
}
