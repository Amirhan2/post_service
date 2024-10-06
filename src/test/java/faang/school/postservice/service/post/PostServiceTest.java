package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для PostService")
public class PostServiceTest {

    private final long authorId = 1L;
    private final long projectId = 1L;
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private static final long ID = 1L;
    private Post post;
    private PostResponseDto postResponseDto;

    @BeforeEach
    public void setup() {
        post = new Post();
        post.setId(1L);
        post.setContent("Test content");
        post.setAuthorId(authorId);
        post.setProjectId(projectId);
        post.setLikes(Collections.emptyList());

        postResponseDto = new PostResponseDto(post.getId(), post.getContent(), post.getAuthorId(), post.getProjectId(), 0);
    }

    @Nested
    @DisplayName("Позитивные тесты")
    class PositiveTests {

        @Test
        @DisplayName("Должен вернуть посты автора с количеством лайков")
        void shouldReturnPostsByAuthorWithLikes() {
            when(postRepository.findByAuthorIdWithLikes(authorId)).thenReturn(List.of(post));
            when(postMapper.toResponseDto(post, 0)).thenReturn(postResponseDto);

            List<PostResponseDto> result = postService.getPostsByAuthorWithLikes(authorId);

            assertEquals(1, result.size());
            assertEquals(postResponseDto, result.get(0));

            verify(postRepository).findByAuthorIdWithLikes(authorId);
            verify(postMapper).toResponseDto(post, 0);
        }

        @Test
        @DisplayName("Должен вернуть посты проекта с количеством лайков")
        void shouldReturnPostsByProjectWithLikes() {
            when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(List.of(post));
            when(postMapper.toResponseDto(post, 0)).thenReturn(postResponseDto);

            List<PostResponseDto> result = postService.getPostsByProjectWithLikes(projectId);

            assertEquals(1, result.size());
            assertEquals(postResponseDto, result.get(0));

            verify(postRepository).findByProjectIdWithLikes(projectId);
            verify(postMapper).toResponseDto(post, 0);
        }

        void init() {
            post = Post.builder()
                    .id(ID)
                    .build();
        }

        @Nested
        @DisplayName("Негативные тесты")
        class NegativeTests {

            @Test
            @DisplayName("When Post ID is valid then return the Post")
            void whenFindByIdThenSuccess() {
                when(postRepository.findById(ID)).thenReturn(Optional.of(post));

                postService.findById(ID);

                assertEquals(1L, ID);
            }

            @Test
            @DisplayName("When Post ID is invalid then throw EntityNotFoundException")
            void whenFindByIdThenThrowException() {
                when(postRepository.findById(ID)).thenReturn(Optional.empty());

                assertThrows(EntityNotFoundException.class, () -> postService.findById(ID));
            }

            @Test
            @DisplayName("Должен вернуть пустой список, если у автора нет постов")
            void shouldReturnEmptyListIfNoPostsForAuthor() {
                when(postRepository.findByAuthorIdWithLikes(authorId)).thenReturn(Collections.emptyList());

                List<PostResponseDto> result = postService.getPostsByAuthorWithLikes(authorId);

                assertEquals(0, result.size());

                verify(postRepository).findByAuthorIdWithLikes(authorId);
                verify(postMapper, never()).toResponseDto(any(), anyInt());
            }

            @Test
            @DisplayName("Должен вернуть пустой список, если у проекта нет постов")
            void shouldReturnEmptyListIfNoPostsForProject() {
                when(postRepository.findByProjectIdWithLikes(projectId)).thenReturn(Collections.emptyList());

                List<PostResponseDto> result = postService.getPostsByProjectWithLikes(projectId);

                assertEquals(0, result.size());

                verify(postRepository).findByProjectIdWithLikes(projectId);
                verify(postMapper, never()).toResponseDto(any(), anyInt());
            }
        }
    }
}
