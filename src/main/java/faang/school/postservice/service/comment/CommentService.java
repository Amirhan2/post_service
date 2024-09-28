package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.event.user.UserCacheEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.producer.user.UserCacheProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final ModerationDictionary moderationDictionary;
    private final UserCacheProducer userCacheProducer;
    private final ExecutorService moderationExecutor;

    @Value("${comment.batchSize}")
    private int batchSize;

    public CommentService(
            CommentRepository commentRepository,
            CommentMapper commentMapper,
            CommentValidator commentValidator,
            ModerationDictionary moderationDictionary,
            UserCacheProducer userCacheProducer,
            @Qualifier("moderation-thread-pool") ExecutorService moderationExecutor
    ) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.commentValidator = commentValidator;
        this.moderationDictionary = moderationDictionary;
        this.userCacheProducer = userCacheProducer;
        this.moderationExecutor = moderationExecutor;
    }

    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        commentValidator.findPostById(postId);
        Comment savedComment = commentRepository.save(commentMapper.toEntity(commentDto));

        userCacheProducer.sendEvent(new UserCacheEvent(savedComment.getAuthorId()));

        return commentMapper.toDto(savedComment);
    }

    @Transactional
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto) {
        commentValidator.findPostById(postId);
        Comment comment = commentValidator.findCommentById(commentId);
        commentValidator.checkUserRightsToChangeComment(comment, commentDto);
        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPost(Long postId) {
        commentValidator.findPostById(postId);
        List<Comment> comments = commentRepository.findAllByPostIdSorted(postId);
        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentValidator.findCommentById(commentId);
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void verifyComments() {
        List<Comment> commentsToVerify = commentRepository.findAllByVerifiedDateIsNull();

        List<List<Comment>> partitions = partitionList(commentsToVerify, batchSize);

        List<CompletableFuture<Void>> futures = partitions.stream()
                .map(this::moderatePartition)
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private CompletableFuture<Void> moderatePartition(List<Comment> partition) {
        return CompletableFuture.runAsync(() -> {
            for (Comment comment : partition) {
                boolean containsBadWords = moderationDictionary.containsForbiddenWords(comment.getContent());
                comment.setVerified(!containsBadWords);
                comment.setVerifiedDate(LocalDateTime.now());
            }
            commentRepository.saveAll(partition);
        }, moderationExecutor);
    }

    private List<List<Comment>> partitionList(List<Comment> list, int partitionSize) {
        List<List<Comment>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += partitionSize) {
            partitions.add(new ArrayList<>(list.subList(i, Math.min(list.size(), i + partitionSize))));
        }
        return partitions;
    }
}
