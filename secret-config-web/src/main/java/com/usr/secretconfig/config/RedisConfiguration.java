package com.usr.secretconfig.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author dht31261
 * @date 2025年10月12日 20:12:51
 */
@Configuration
public class RedisConfiguration {

    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        return redisTemplate;
    }

    @Bean
    RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizer() {
        return configuration -> {
            if (StringUtils.isBlank(configuration.useSingleServer().getPassword())) {
                configuration.useSingleServer().setPassword(null);
            }
        };
    }
}