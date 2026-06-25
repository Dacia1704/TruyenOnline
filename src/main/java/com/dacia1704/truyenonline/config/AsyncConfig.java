package com.dacia1704.truyenonline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "cloudinaryExecutor")
    public Executor cloudinaryExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Số luồng (thread) sẵn sàng
        executor.setCorePoolSize(10);
        // Số luồng tối đa được phình ra khi có quá nhiều người upload cùng lúc
        executor.setMaxPoolSize(50);
        // Số lượng file phải xếp hàng chờ nếu 50 luồng kia đều đang bận
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Cloudinary-Upload-");
        executor.initialize();
        return executor;
    }
}