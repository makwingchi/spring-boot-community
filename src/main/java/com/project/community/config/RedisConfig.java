package com.project.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-18 17:02
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // set key serializer
        template.setKeySerializer(RedisSerializer.string());
        // set value serializer
        template.setValueSerializer(RedisSerializer.json());
        // set hash key serializer
        template.setHashKeySerializer(RedisSerializer.string());
        // set value of hash serializer
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();

        return template;
    }

}
