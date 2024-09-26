package com.dnd.runus.application.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Bean(destroyMethod = "shutdown")
    ExecutorService virtualExecutorService() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    @Bean(name = "virtualThreadExecutor")
    public Executor getAsyncExecutor() {
        return new TaskExecutorAdapter(virtualExecutorService());
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            String parameterValues =
                    Stream.ofNullable(params).map(Object::toString).collect(Collectors.joining(", "));

            log.error(
                    "Exception Name - {}, Exception message - {}, Method name - {}, Parameter values - {}",
                    throwable.getClass().getName(),
                    throwable.getMessage(),
                    method.getName(),
                    parameterValues);
            try {
                throw (Exception) throwable;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
