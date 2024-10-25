package faang.school.postservice.producer;

import faang.school.postservice.event.kafka.KafkaPostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostEventProducer extends AbstractProducer<KafkaPostEvent> {
    public KafkaPostEventProducer(NewTopic postsTopic,
                                  KafkaTemplate<String, Object> kafkaTemplate) {
        super(postsTopic, kafkaTemplate);
    }
}
