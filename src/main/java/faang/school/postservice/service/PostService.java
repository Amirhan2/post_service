package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.AlreadyDeletedException;
import faang.school.postservice.exception.AlreadyPostedException;
import faang.school.postservice.exception.NoPublishedPostException;
import faang.school.postservice.exception.SamePostAuthorException;
import faang.school.postservice.exception.UpdatePostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userService;
    private final ProjectServiceClient projectService;
    private final PostMapper postMapper;
    private final ThreadPoolExecutor threadPoolExecutor;

    public PostDto crateDraftPost(PostDto postDto) {
        validateData(postDto);

        Post savedPost = postRepository.save(postMapper.toEntity(postDto));
        log.info("Draft post was created successfully, draftId={}", savedPost.getId());
        return postMapper.toDto(savedPost);
    }

    public PostDto publishPost(long postId) {
        Post post = validatePostId(postId);

        if (post.isPublished() || (post.getScheduledAt() != null
                && post.getScheduledAt().isAfter(LocalDateTime.now()))) {
            throw new AlreadyPostedException("You cannot published post, that had been already published");
        }
        if (post.isDeleted()) {
            throw new AlreadyDeletedException(("You cannot publish post, that had been deleted"));
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        log.info("Post was published successfully, postId={}", post.getId());
        return postMapper.toDto(post);
    }

    public PostDto updatePost(PostDto updatePost) {
        long postId = updatePost.getId();
        Post post = validatePostId(postId);
        validateAuthorUpdate(post, updatePost);
        validateScheduleAt(post, updatePost);
        post.setContent(updatePost.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        log.info("Post was updated successfully, postId={}", post.getId());
        return postMapper.toDto(post);
    }

    public PostDto softDelete(long postId) {
        Post post = validatePostId(postId);

        if (post.isDeleted()) {
            throw new AlreadyDeletedException("Post has been already deleted");
        }
        post.setDeleted(true);
        log.info("Post was soft-deleted successfully, postId={}", postId);
        return postMapper.toDto(post);
    }

    public PostDto getPost(long postId) {
        Post post = validatePostId(postId);

        if (post.isDeleted()) {
            throw new AlreadyDeletedException("This post has been already deleted");
        }
        if (!post.isPublished()) {
            throw new NoPublishedPostException("This post hasn't been published yet");
        }

        log.info("Post has taken from DB successfully, postId={}", postId);
        return postMapper.toDto(post);
    }

    public List<PostDto> getUserDrafts(long userId) {
        validateUserId(userId);

        List<PostDto> userDrafts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        log.info("User's drafts have taken from DB successfully, userId={}", userId);
        return userDrafts;
    }

    public List<PostDto> getProjectDrafts(long projectId) {
        validateProjectId(projectId);

        List<PostDto> projectDrafts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        log.info("Drafts of project have taken from DB successfully, projectId={}", projectId);
        return projectDrafts;
    }

    public List<PostDto> getUserPosts(long userId) {
        validateUserId(userId);

        List<PostDto> userPosts = postRepository.findByAuthorIdWithLikes(userId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        log.info("User's posts have taken from DB successfully, userId={}", userId);
        return userPosts;
    }

    public List<PostDto> getProjectPosts(long projectId) {
        validateProjectId(projectId);

        List<PostDto> projectPosts = postRepository.findByProjectIdWithLikes(projectId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        log.info("Posts of project have taken from DB successfully, projectId={}", projectId);
        return projectPosts;
    }

    public void publishScheduledPosts() {
        Iterable<Post> unFilteredPosts = postRepository.findAll();
        List<Post> filteredPosts = filterPostsByPublicationDate(unFilteredPosts);
        publishPosts(filteredPosts);
        log.info("Scheduled post publishing executed");
    }

    private void publishPosts(List<Post> filteredPosts) {
        if (filteredPosts.size() > 1000) {
            int batchSize = 1000;

            for (int i = 0; i < filteredPosts.size(); i += batchSize) {
                int startIndex = i;
                int endIndex = Math.min(i + batchSize, filteredPosts.size());
                threadPoolExecutor.execute(() -> processAndSavePosts(filteredPosts.subList(startIndex, endIndex)));
            }
            threadPoolExecutor.shutdown();
        } else {
            processAndSavePosts(filteredPosts);
        }
    }

    private void processAndSavePosts(List<Post> posts) {
        posts.forEach(post -> {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        });
        postRepository.saveAll(posts);
    }

    private List<Post> filterPostsByPublicationDate(Iterable<Post> unFilteredPosts) {
        return StreamSupport.stream(unFilteredPosts.spliterator(), false)
                .filter(post -> !post.isPublished())
                .filter(post -> !post.isDeleted())
                .filter(post -> {
                    LocalDateTime postSchedule = post.getScheduledAt();
                    LocalDateTime now = LocalDateTime.now();
                    return postSchedule.isBefore(now) || postSchedule.isEqual(now);
                })
                .toList();
    }

    private Post validatePostId(long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("This post does not exist"));
    }

    private void validateScheduleAt(Post post, PostDto updatePost) {
        LocalDateTime updateScheduleAt = updatePost.getScheduledAt();

        if (updateScheduleAt != null && updateScheduleAt.isAfter(post.getScheduledAt())) {
            post.setScheduledAt(updateScheduleAt);
        }
    }

    private void validateData(PostDto postDto) {
        Long userId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        if (userId != null && projectId != null) {
            throw new SamePostAuthorException("The author of the post cannot be both a user and a project");
        }
        if (userId != null) {
            validateUserId(userId);
        } else {
            validateProjectId(projectId);
        }
    }

    private void validateAuthorUpdate(Post post, PostDto updatePost) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();
        Long updateAuthorId = updatePost.getAuthorId();
        Long updateProjectId = updatePost.getProjectId();

        if (authorId != null) {
            if (updateAuthorId == null || updateAuthorId != authorId) {
                throw new UpdatePostException("Author of the post cannot be deleted or changed");
            }
        } else {
            if (updateProjectId == null || updateProjectId != projectId) {
                throw new UpdatePostException("Author of the post cannot be deleted or changed");
            }
        }
    }

    private void validateUserId(long id) {
        try {
            userService.getUser(id);
        } catch (FeignException e) {
            throw new EntityNotFoundException("This user is not found");
        }
    }

    private void validateProjectId(long id) {
        try {
            projectService.getProject(id);
        } catch (FeignException e) {
            throw new EntityNotFoundException("This project is not found");
        }
    }
}