package com.questionnaire.common.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 */
@Configuration
//读取配置文件的前缀
@ConfigurationProperties("spring.redis")
@Data
public class RedissonConfig {

    private  Integer database;
    private  String host;
    private Integer port;
    private String password;


    @Bean
    public RedissonClient redissonClient() {
        // Create config object，创建配置
        Config config = new Config();

        //设置redis地址以及使用哪个服务器
        String redisAddress = String.format("redis://%s:%d",host,port);
        config.useSingleServer()
                .setAddress(redisAddress)
                .setDatabase(1);
//                .setPassword(password);

        // 2. Create Redisson instance
        // Sync and Async API
        RedissonClient redisson = Redisson.create(config);

        return redisson;

    }
}