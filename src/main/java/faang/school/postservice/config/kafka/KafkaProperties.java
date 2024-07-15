package faang.school.postservice.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private String host;

    private String port;

    private TopicsNames topicsNames;


    public String getAddress() {
        return String.format("%s:%s", host, port);
    }

    @Data
    static class TopicsNames {
        private String postTopic;
    }
}