package faang.school.postservice;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.PostRequirementsException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.ContentModerationService;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private UserContext userContext;

    @Mock
    private ContentModerationService contentModerationService;

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    PostService postService;


    private Post post;


    @BeforeEach
    public void setUp() throws Exception {
        post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        post.setContent("Sample content");
        post.setPublished(false);
    }

    @Test
    public void testCreateDraftPost() {
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.createDraftPost(post);
        assertFalse(result.isPublished(), "The post should be marked as draft (not published).");

        verify(postRepository, times(1)).save(post);
        assertNotNull(result);
    }

    @Test
    public void testCreateDraftPostCallsValidateUserExists() {
        post.setAuthorId(1L);
        post.setProjectId(null);

        postService.createDraftPost(post);

        verify(userContext, times(1)).setUserId(post.getAuthorId());
        verify(userServiceClient, times(1)).getUser(post.getAuthorId());
    }

    @Test
    public void testCreateDraftPostCallsValidateProjectExists() {
        post.setAuthorId(null);
        post.setProjectId(1L);

        postService.createDraftPost(post);

        verify(projectServiceClient, times(1)).getProject(post.getProjectId());
    }


    @Test
    public void testUpdatePost() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        postService.updatePost(post.getId(), post.getContent());

        verify(postRepository, times(1)).save(post);
    }


    @Test
    public void testDeletePost() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        postService.deletePost(post.getId());

        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void testGetPostById_Success() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        Post result = postService.getPostById(post.getId());

        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @Test
    public void testGetPostById_NotFound() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.empty());

        assertThrows(PostRequirementsException.class, () -> postService.getPostById(post.getId()));
        verify(postRepository, times(1)).findById(post.getId());
    }

    @Test
    public void testGetUserDrafts_Success() {
        List<Post> drafts = new ArrayList<>();
        drafts.add(post);
        when(postRepository.findDraftsByAuthorId(post.getAuthorId())).thenReturn(drafts);

        List<Post> result = postService.getUserDrafts(post.getAuthorId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(postRepository, times(1)).findDraftsByAuthorId(post.getAuthorId());
    }

    @Test
    public void testGetProjectDrafts_Success() {
        post.setProjectId(1L);

        List<Post> drafts = new ArrayList<>();
        drafts.add(post);

        when(postRepository.findDraftsByProjectId(post.getProjectId())).thenReturn(drafts);

        List<Post> result = postService.getProjectDrafts(post.getProjectId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getProjectId());
        verify(postRepository, times(1)).findDraftsByProjectId(post.getProjectId());
    }

    @Test
    public void testGetUserPublishedPosts_Success() {
        post.setPublished(true);
        List<Post> publishedPosts = new ArrayList<>();
        publishedPosts.add(post);
        when(postRepository.findPublishedByAuthorId(post.getAuthorId())).thenReturn(publishedPosts);

        List<Post> result = postService.getUserPublishedPosts(post.getAuthorId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(postRepository, times(1)).findPublishedByAuthorId(post.getAuthorId());
    }

    @Test
    public void testGetProjectPublishedPosts_Success() {
        post.setProjectId(1L);
        post.setPublished(true);

        List<Post> publishedPosts = new ArrayList<>();
        publishedPosts.add(post);

        when(postRepository.findPublishedByProjectId(post.getProjectId())).thenReturn(publishedPosts);

        List<Post> result = postService.getProjectPublishedPosts(post.getProjectId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getProjectId());
        verify(postRepository, times(1)).findPublishedByProjectId(post.getProjectId());
    }

    @Test
    public void testModerationOfPost() {
        List<Post> unverifiedPosts = new ArrayList<>();
        unverifiedPosts.add(post);

        when(postRepository.findUnverifiedOrOldVerifiedPosts(any(LocalDateTime.class))).thenReturn(unverifiedPosts);
        when(contentModerationService.checkContentAndModerate(any(Post.class))).thenReturn(CompletableFuture.completedFuture(null));

        postService.moderationOfPost();

        verify(postRepository, times(1)).findUnverifiedOrOldVerifiedPosts(any(LocalDateTime.class));
        verify(contentModerationService, times(1)).checkContentAndModerate(post);
    }


}
