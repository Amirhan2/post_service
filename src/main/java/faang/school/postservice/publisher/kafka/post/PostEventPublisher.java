package faang.school.postservice.publisher.kafka.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.publisher.kafka.AbstractEventKafkaPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends AbstractEventKafkaPublisher<PostEvent> {

    public PostEventPublisher(KafkaTemplate<String, String> kafkaTemplate, UserServiceClient userServiceClient) {
        super(kafkaTemplate,userServiceClient);

    }
}
