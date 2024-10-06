package faang.school.postservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("spring.data.redis")
public class RedisProperties {

    private int port;
    private String host;
}
