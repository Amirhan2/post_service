package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {
    private CommentMapperImpl commentMapper = new CommentMapperImpl();

    @BeforeEach
    void setUp() {

    }

    @Test
    void mapToComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(10L);
        commentDto.setContent("someContent");
        commentDto.setPostId(11L);
        commentDto.setCreatedAt(LocalDateTime.now());
        commentDto.setUpdatedAt(LocalDateTime.now());
        Comment comment = commentMapper.mapToComment(commentDto);
        assertAll(
                () -> assertEquals(commentDto.getAuthorId(), comment.getAuthorId()),
                () -> assertEquals(commentDto.getContent(), comment.getContent()),
                () -> assertEquals(commentDto.getPostId(), comment.getPost().getId()),
                () -> assertEquals(commentDto.getCreatedAt(), comment.getCreatedAt()),
                () -> assertEquals(commentDto.getUpdatedAt(), comment.getUpdatedAt())
        );
    }

    @Test
    void mapToCommentDto() {
        Post post = new Post();
        post.setId(11L);
        Comment comment = new Comment();
        comment.setAuthorId(10L);
        comment.setContent("someContent");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setPost(post);
        CommentDto commentDto = commentMapper.mapToCommentDto(comment);

        assertAll(
                () -> assertEquals(comment.getAuthorId(), commentDto.getAuthorId()),
                () -> assertEquals(comment.getContent(), commentDto.getContent()),
                () -> assertEquals(comment.getPost().getId(), commentDto.getPostId()),
                () -> assertEquals(comment.getCreatedAt(), commentDto.getCreatedAt()),
                () -> assertEquals(comment.getUpdatedAt(), commentDto.getUpdatedAt())
        );
    }
}