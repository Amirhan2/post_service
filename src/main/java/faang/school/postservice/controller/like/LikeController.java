package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeServiceImpl likeService;


    @PostMapping("/commentId/{commentId}")
    public LikeDto createLikeComment(@PathVariable @Positive long commentId) {
        return likeService.createLikeComment(commentId);
    }

    @DeleteMapping("/commentId/{commentId}")
    public ResponseEntity<String> deleteLikeComment(@PathVariable @Positive long commentId) {
        likeService.deleteLikeComment(commentId);
        return ResponseEntity.ok("Comment with id " + commentId + " was deleted successfully.");
    }

    @PostMapping("/postId/{postId}")
    public LikeDto createLikePost(@PathVariable @Positive long postId) {
        return likeService.createLikePost(postId);
    }

    @DeleteMapping("/postId/{postId}")
    public ResponseEntity<String> deleteLikePost(@PathVariable @Positive long postId) {
        likeService.deleteLikePost(postId);
        return ResponseEntity.ok("Post with id " + postId + " was deleted successfully.");
    }
}
