package com.dnd.runus.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultExecuteListenerProvider;
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
@Slf4j
@TestConfiguration
@RequiredArgsConstructor
public class TestJooqConfig {
    private final DataSource dataSource;
    private final JooqProperties jooqProperties;

    static {
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");
    }

    @Bean
    DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    DSLContext dsl() {
        Configuration configuration = new DefaultConfiguration()
                .set(connectionProvider())
                .set(jooqProperties.determineSqlDialect(dataSource))
                .set(new DefaultExecuteListenerProvider(new LoggingExecuteListener())); // For logging SQL
        return DSL.using(configuration);
    }

    public static class LoggingExecuteListener implements ExecuteListener {
        @Override
        public void executeStart(ExecuteContext ctx) {
            log.info("jOOQ: {}", ctx.query());
        }
    }
}
