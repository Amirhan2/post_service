package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import java.util.List;

public interface HashtagService {
    void createHashtags(Post post);

    List<PostDto> findPostsByHashtag(String hashtag);

    void updateHashtags(Post post);
}
