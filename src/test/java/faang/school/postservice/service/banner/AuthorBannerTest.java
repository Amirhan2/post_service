package faang.school.postservice.service.banner;

import faang.school.postservice.publisher.RedisMessagePublisher;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorBannerTest {
    @Mock
    private PostService postService;
    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @InjectMocks
    private AuthorBanner authorBanner;

    @Test
    void publishingUsersForBan() {
        List<Long> userIds = List.of(1L, 2L, 5L, 11L, 25L, 12L);
        when(postService.findUserIdsForBan()).thenReturn(userIds);

        authorBanner.publishingUsersForBan();

        userIds.forEach(userId ->
                verify(redisMessagePublisher, Mockito.times(1))
                        .publish(String.valueOf(userId)));
    }
}