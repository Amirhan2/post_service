package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Boolean published;
    private Boolean deleted;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
