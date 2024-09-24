package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.postservice.util.post.PostCacheFabric.buildHashTags;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDto;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDtoWithTags;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDtosWithTags;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostCacheServiceTest {
    private static final int NUMBER_OF_TAGS = 3;
    private static final int NUMBER_OF_POSTS = NUMBER_OF_TAGS;
    private static final long USER_ID = 1L;
    private static final int NEVER = 0;
    private static final String TAG = "java";
    private static final int START = 0;
    private static final int END = 9;

    @Mock
    private PostCacheOperations postCacheOperations;

    @InjectMocks
    private PostCacheService postCacheService;

    private final List<String> hashTags = buildHashTags(NUMBER_OF_TAGS);
    private final List<String> emptyHashTags = List.of();
    private final PostCacheDto postWithTags = buildPostCacheDtoWithTags(USER_ID, hashTags);
    private final PostCacheDto postEmptyTags = buildPostCacheDtoWithTags(USER_ID, emptyHashTags);
    private final PostCacheDto post = buildPostCacheDto(USER_ID);
    private final List<PostCacheDto> postsWithTags = buildPostCacheDtosWithTags(NUMBER_OF_POSTS);

    @Test
    @DisplayName("Given post with no new tags when check then will not add to cache")
    void testNewPostProcessNoNewTags() {
        postCacheService.newPostProcess(postEmptyTags);
        verify(postCacheOperations, times(NEVER)).addPostToCache(postEmptyTags, postEmptyTags.getHashTags());
    }

    @Test
    @DisplayName("Given post with new tags and add post to cache")
    void testNewPostProcessSuccessful() {
        postCacheService.newPostProcess(postWithTags);
        verify(postCacheOperations).addPostToCache(postWithTags, postWithTags.getHashTags());
    }

    @Test
    @DisplayName("Given post with no primal tags when check then will not delete from cache")
    void testDeletePostProcessNoPrimalTags() {
        postCacheService.deletePostProcess(post, emptyHashTags);
        verify(postCacheOperations, times(NEVER)).deletePostOfCache(post, emptyHashTags);
    }

    @Test
    @DisplayName("Given post with primal tags and delete from cache")
    void testDeletePostProcessSuccessful() {
        postCacheService.deletePostProcess(post, hashTags);
        verify(postCacheOperations).deletePostOfCache(post, hashTags);
    }

    @Test
    @DisplayName("Update post process no tags post")
    void testUpdatePostProcessNothingToDo() {
        postCacheService.updatePostProcess(postEmptyTags, emptyHashTags);

        verify(postCacheOperations, times(NEVER)).addPostToCache(postEmptyTags, emptyHashTags);
        verify(postCacheOperations, times(NEVER)).deletePostOfCache(postEmptyTags, emptyHashTags);
        verify(postCacheOperations, times(NEVER)).updatePostOfCache(post, emptyHashTags, emptyHashTags);
    }

    @Test
    @DisplayName("Given empty primal and ful upd tags and add post to cache")
    void testUpdatePostProcessAddPostToCache() {
        postCacheService.updatePostProcess(postWithTags, emptyHashTags);

        verify(postCacheOperations).addPostToCache(postWithTags, postWithTags.getHashTags());
    }

    @Test
    @DisplayName("Given ful primal and empty upd tags and delete post from cache")
    void testUpdatePostProcessDelete() {
        postCacheService.updatePostProcess(postEmptyTags, hashTags);

        verify(postCacheOperations).deletePostOfCache(postEmptyTags, hashTags);
    }

    @Test
    @DisplayName("Given ful primal and ful upd tags and update post in cache")
    void testUpdatePostProcessUpdate() {
        postCacheService.updatePostProcess(postWithTags, hashTags);

        verify(postCacheOperations).updatePostOfCache(postWithTags, hashTags, postWithTags.getHashTags());
    }

    @Test
    @DisplayName("Given list of posts and add each to cache by tag to find")
    void testAddListOfPostsToCacheSuccessful() {
        postCacheService.addListOfPostsToCache(postsWithTags, TAG);

        verify(postCacheOperations, times(postsWithTags.size()))
                .addPostToCacheByTag(any(PostCacheDto.class), anyList(), anyString());
    }

    @Test
    @DisplayName("Given ")
    void testFindInRangeByHashTagSuccessful() {
        postCacheService.findInRangeByHashTag(TAG, START, END);

        verify(postCacheOperations).findIdsByHashTag(TAG, START, END);
        verify(postCacheOperations).findAllByIds(anyList());
    }
}












