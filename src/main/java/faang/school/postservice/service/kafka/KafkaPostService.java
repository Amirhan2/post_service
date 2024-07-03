package faang.school.postservice.service.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.event.post.PostEventDto;
import faang.school.postservice.publisher.kafka.post.PostEventPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaPostService {

    private final UserServiceClient userServiceClient;
    private final PostEventPublisher postEventPublisher;

    @Value("${spring.data.kafka.batch-followers}")
    private int batchSize;

    @Retryable(retryFor = FeignException.class, maxAttempts = 5)
    public void sendPostToKafka(Long id) {
        List<Long> followers = userServiceClient.getFollowerIdsById(id);
        ListUtils.partition(followers, batchSize).forEach(followersPartition -> {
                    PostEventDto postEventDto = PostEventDto.builder()
                            .authorSubscriberIds(followersPartition)
                            .build();
                    postEventPublisher.publish(postEventDto);
                }
        );
    }
}
