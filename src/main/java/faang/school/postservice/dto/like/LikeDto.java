package faang.school.postservice.dto.like;

import lombok.Data;

@Data
public class LikeDto {
    private Long id;
    private Long userId;
    private Long commentId;
    private Long postId;
}
