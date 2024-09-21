package faang.school.postservice.controller;

import faang.school.postservice.model.post.CachePost;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping
    public List<CachePost> getFeed(@RequestParam(required = false) Long lastPostId) {
        return feedService.getFeed(lastPostId);
    }
}
