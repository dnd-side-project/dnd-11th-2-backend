package com.dnd.runus.infrastructure.persistence.annotation;

import com.dnd.runus.config.TestJooqConfig;
import com.dnd.runus.config.TestcontainersConfig;
import com.dnd.runus.infrastructure.persistence.InfrastructurePersistencePackage;
import com.dnd.runus.infrastructure.persistence.jooq.JooqConfig;
import com.dnd.runus.infrastructure.persistence.jpa.JpaConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testcontainers
@DataJpaTest
@ComponentScan(basePackageClasses = {InfrastructurePersistencePackage.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // H2 사용 금지
@Import({
    TestcontainersConfig.class,
    JpaConfig.class,
    JooqConfig.class,
    TestJooqConfig.class,
})
public @interface RepositoryTest {}
