package com.dnd.runus.infrastructure.persistence.annotation;

import com.dnd.runus.config.TestcontainersConfig;
import com.dnd.runus.global.config.JpaConfig;
import com.dnd.runus.infrastructure.persistence.InfrastructurePersistencePackage;
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
@Import({JpaConfig.class, TestcontainersConfig.class})
public @interface RepositoryTest {}
