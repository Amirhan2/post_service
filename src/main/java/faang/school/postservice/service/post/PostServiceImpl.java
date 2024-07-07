package faang.school.postservice.service.post;

import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostHashtagDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerificationStatus;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.hashtag.async.AsyncHashtagService;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final AsyncHashtagService asyncHashtagService;
    private final ModerationDictionary moderationDictionary;

    @Override
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Post with id %s not found", id)));
    }

    @Override
    @Transactional
    public PostDto create(PostCreateDto postCreateDto) {
        postValidator.validateAuthor(postCreateDto.getAuthorId(), postCreateDto.getProjectId());
        postValidator.validatePostContent(postCreateDto.getContent());
        Post post = postRepository.save(postMapper.toEntity(postCreateDto));
        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto publish(Long id) {
        Post post = findById(id);
        postValidator.validatePublicationPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post = postRepository.save(post);

        PostHashtagDto postHashtagDto = postMapper.toHashtagDto(post);
        asyncHashtagService.addHashtags(postHashtagDto);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto update(Long id, PostUpdateDto postUpdateDto) {
        Post post = findById(id);
        postValidator.validatePostContent(post.getContent());
        post.setContent(postUpdateDto.getContent());
        post = postRepository.save(post);

        PostHashtagDto postHashtagDto = postMapper.toHashtagDto(post);
        asyncHashtagService.updateHashtags(postHashtagDto);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Post post = findById(id);
        post.setDeleted(true);
        postRepository.save(post);

        PostHashtagDto postHashtagDto = postMapper.toHashtagDto(post);
        asyncHashtagService.removeHashtags(postHashtagDto);
    }

    @Override
    public List<PostDto> findAllByHashtag(String hashtag, Pageable pageable) {
        return asyncHashtagService.getPostsByHashtag(hashtag, pageable).join().stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> findPostDraftsByUserAuthorId(Long id) {
        return postRepository.findByAuthorIdAndPublishedAndDeletedWithLikes(id, false, false).stream()
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<PostDto> findPostDraftsByProjectAuthorId(Long id) {
        return postRepository.findByProjectIdAndPublishedAndDeletedWithLikes(id, false, false).stream()
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<PostDto> findPostPublicationsByUserAuthorId(Long id) {
        return postRepository.findByAuthorIdAndPublishedAndDeletedWithLikes(id, true, false).stream()
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .toList();
    }

    @Override
    public List<PostDto> findPostPublicationsByProjectAuthorId(Long id) {
        return postRepository.findByProjectIdAndPublishedAndDeletedWithLikes(id, true, false).stream()
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getPublishedAt).reversed())
                .toList();
    }

    @Override
    @Async("executorService")
    public void verifyPost(List<Post> posts) {
        for (Post post : posts) {

            if (moderationDictionary.checkCurseWordsInPost(post.getContent())) {
                post.setIsVerify(VerificationStatus.NOT_VERIFIED);
            } else {
                post.setIsVerify(VerificationStatus.VERIFIED);
            }

            post.setVerifiedDate(LocalDateTime.now());
            postRepository.save(post);
        }
    }
}