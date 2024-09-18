package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentServiceValidator validator;
    private final CommentMapper mapper;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public CommentDto createComment(CommentDto commentDto) {
        validator.validatePostExist(commentDto.getPostId());
        validator.validateCommentContent(commentDto.getContent());
        userContext.setUserId(commentDto.getAuthorId());
        userServiceClient.getUser(commentDto.getAuthorId());
        Comment comment = mapper.mapToComment(commentDto);
        return mapper.mapToCommentDto(commentRepository.save(comment));
    }

    public List<CommentDto> getComment(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        List<Comment> commentsSorted = comments.stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt).reversed())
                .toList();
        return mapper.mapToCommentDto(commentsSorted);
    }

    public void deleteComment(Long commentId) {
        validator.validateCommentExist(commentId);
        commentRepository.deleteById(commentId);
    }

    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        validator.validateCommentExist(commentId);
        validator.validateCommentContent(commentDto.getContent());
        userContext.setUserId(commentDto.getAuthorId());
        userServiceClient.getUser(commentDto.getAuthorId());
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        comment.setContent(commentDto.getContent());
        return mapper.mapToCommentDto(commentRepository.save(comment));
    }
}
