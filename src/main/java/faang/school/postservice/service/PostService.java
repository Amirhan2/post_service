package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.exception.PublishPostException;
import faang.school.postservice.util.validator.PostServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostServiceValidator validator;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Transactional
    public PostDto addPost(PostDto dto) {
        validator.validateToAdd(dto);

        if (dto.getAuthorId() != null) {
            userServiceClient.getUser(dto.getAuthorId()); // если такого пользователя или эндпоинта нет, то выбросит FeignException, я его поймаю в ExceptionHandler
        }
        if (dto.getProjectId() != null) {
            projectServiceClient.getProject(dto.getProjectId());
        }

        Post post = postMapper.toEntity(dto);
        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publishPost(Long id) {
        Post postById = postRepository.findById(id)
                .orElseThrow(() -> new PublishPostException("Post not found"));

        validator.validateToPublish(postById);

        postById.setPublished(true);
        postById.setPublishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        postRepository.save(postById);

        return postMapper.toDto(postById);
    }
}
