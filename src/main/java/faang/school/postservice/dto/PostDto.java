package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;

    @NotBlank(message = "Content is required")
    private String content;

    private Long authorId;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private boolean published;
    private boolean deleted;

    private List<Long> resourceIds;
}
