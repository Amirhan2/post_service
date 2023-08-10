package faang.school.postservice.service;

import faang.school.postservice.exсeption.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    public Post getPost(long postId){
        return postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post with this Id does not exist !"));
    }

}
