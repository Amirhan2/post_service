package faang.school.postservice.config;

import faang.school.postservice.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public RedisTemplate<String, List<Long>> redisTemplate(RedisConnectionFactory connection) {
        RedisTemplate<String, List<Long>> redisTemplate = new RedisTemplate<>();
        var serializer = new Jackson2JsonRedisSerializer<>(List.class);
        redisTemplate.setConnectionFactory(connection);
        redisTemplate.setValueSerializer(serializer);
        return redisTemplate;
    }
}