package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import feign.FeignException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostService postService;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private CommentService commentService;

    private Long authorId;
    private Long postId;
    private Long commentId;
    private CommentDto commentDto;
    private Post post;
    private Comment comment;
    private List<Comment> comments;
    private List<CommentDto> commentDtos;


    @BeforeEach
    void setup() {
        authorId = 1L;
        postId = 1L;
        commentId = 1L;
        comments = new ArrayList<>();
        comments.add(comment);
        commentDtos = new ArrayList<>();
        commentDtos.add(commentDto);
        commentDto = CommentDto.builder()
                .id(commentId)
                .postId(postId)
                .authorId(authorId)
                .build();
        post = Post.builder()
                .id(postId)
                .comments(comments)
                .build();
        comment = Comment.builder()
                .id(commentId)
                .post(post)
                .authorId(authorId)
                .build();
    }

    @Test
    void testCreateCommentNegative() {
        doThrow(FeignException.FeignClientException.class).when(userServiceClient).getUser(authorId);
        assertThrows(FeignException.FeignClientException.class, () -> commentService.createComment(commentDto));
        verify(commentRepository, times(0)).save(comment);
    }

    @Test
    void testCreateCommentPositive() {
        when(postService.getPostById(commentDto.getPostId())).thenReturn(post);
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        commentService.createComment(commentDto);
        verify(userServiceClient, times(1)).getUser(authorId);
        verify(postService, times(1)).getPostById(postId);
        verify(commentMapper, times(1)).toEntity(commentDto);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testUpdateComment() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        commentService.updateComment(commentDto);
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testGetAllByPostIdPositive() {
        when(postService.getPostById(postId)).thenReturn(post);
        when(commentMapper.toDtos(comments)).thenReturn(commentDtos);
        commentService.getAllByPostId(postId);
        verify(postService, times(1)).getPostById(postId);
        verify(commentMapper, times(1)).toDtos(comments);
    }

    @Test
    void testDeleteComment() {
        commentService.deleteComment(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }


}