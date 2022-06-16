package com.example.simpleshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ThreadPoolTaskSchedulerConfig {

    private static final int THREAD_POOL_SIZE = 2;
    private static final String THREAD_POOL_NAME_PREFIX = "ThreadPoolTaskScheduler";

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(THREAD_POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix(THREAD_POOL_NAME_PREFIX);
        return threadPoolTaskScheduler;
    }
}
