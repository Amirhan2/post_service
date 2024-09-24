package faang.school.postservice.dto.post.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PostUpdatingRequest(
        @NotBlank(message = "Post content can't be null or empty")
        String content
) {
}