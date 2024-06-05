package org.example.config;

import org.example.model.LeaderboardModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConnectionConfig {

    @Value("${redis.hostname}")
    private String hostname;
    @Value("${redis.port}")
    private int port;
    @Value("${redis.username}")
    private String username;
    @Value("${redis.password}")
    private String password;

    @Bean
    RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(hostname);
        config.setPort(port);
        config.setUsername(username);
        config.setPassword(password);

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, LeaderboardModel> redisTemplate() {
        RedisTemplate<String, LeaderboardModel> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<LeaderboardModel>(LeaderboardModel.class));

        return redisTemplate;
    }
}