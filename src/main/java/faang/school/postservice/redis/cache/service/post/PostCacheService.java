package faang.school.postservice.redis.cache.service.post;

import faang.school.postservice.redis.cache.entity.PostCache;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PostCacheService {

    CompletableFuture<PostCache> save(PostCache entity, List<Long> subscriberIds);

    void incrementLikes(long postId);

    void incrementViews(long postId);

    void decrementLikes(long postId);

    void deleteById(long postId, List<Long> subscriberIds);
}
