package com.ymy.boot.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ringo
 * @date 2021/4/22 22:26
 */
@Configuration
public class RedisConfig {
    @Bean
    public Redisson redisson() {
        Config config = new Config();
        // 集群: config.useClusterServers().addNodeAddress("redis://127.0.0.1:7181");
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        return (Redisson) Redisson.create(config);
    }
}
