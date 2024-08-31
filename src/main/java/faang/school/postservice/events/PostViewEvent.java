package faang.school.postservice.events;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostViewEvent extends Event {
    private Long postId;
    private Long userId;
    private Long authorId;
    private LocalDateTime viewedAt;
}
