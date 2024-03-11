package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<EventType> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    abstract public void publish(EventType event);

    protected void convertAndSend(EventType event, String channelTopicName) {
        try {
            String json = objectMapper.writeValueAsString(event);
            log.debug("converted event {} to json", event);
            redisTemplate.convertAndSend(channelTopicName, json);
            log.debug("json with event {} sent to topic {}", event, channelTopicName);
        } catch (JsonProcessingException e) {
            log.debug("JsonProcessingException with event {}", event);
            throw new RuntimeException("Cannot serialize event to json");
        }
    }
}
