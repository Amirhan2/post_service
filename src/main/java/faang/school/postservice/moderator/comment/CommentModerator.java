package faang.school.postservice.moderator.comment;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Component
public class CommentModerator {
    @Value("${comment_moderation.chunk_size}")
    private int batchSize;
    CommentRepository commentRepository;
    ModerationDictionary moderationDictionary;

    @Bean
    public ExecutorService moderatorPool() {
        return Executors.newFixedThreadPool(10);
    }

    @Async("moderatorPool")
    @Scheduled(cron = "0 0 8 * * *")
    public void verifyCommentsByDate(LocalDateTime verifiedDate) throws IOException {
        commentRepository.findCommentsByVerifiedDate(verifiedDate)
                .stream()
                .filter(comment -> !comment.isVerified())
                .collect(Collectors.groupingBy(comment -> (int) Math.floor((double) (comment.getId() - 1) / batchSize)))
                .forEach((batch, comments) -> {
                    for (Comment comment : comments) {
                        try {
                            moderationDictionary.verifyComment(comment);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }
}