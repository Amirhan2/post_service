package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;

    @Test
    void testGetPostNull() {
        Long postId = null;
        assertThrows(NullPointerException.class, () -> postService.getPost(postId));
    }

    @Test
    void testGetPostValidate() {
        Post post = Post.builder().id(1L).build();
        Long postId = 1L;

        Mockito.when(postRepository.findById(postId))
                .thenReturn(Optional.ofNullable(post));

        Post result = postService.getPost(postId);

        Mockito.verify(postRepository, Mockito.times(1))
                .findById(postId);

        assertEquals(post, result);
    }
}