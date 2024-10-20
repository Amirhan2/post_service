package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.authorId = :authorId ORDER BY p.createdAt DESC")
    List<Post> findDraftsByAuthorId(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.projectId = :projectId ORDER BY p.createdAt DESC")
    List<Post> findDraftsByProjectId(long projectId);

    @Query("SELECT p FROM Post p WHERE p.published = true AND p.deleted = false AND p.authorId = :authorId ORDER BY p.publishedAt DESC")
    List<Post> findPublishedByAuthorId(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = true AND p.deleted = false AND p.projectId = :projectId ORDER BY p.publishedAt DESC")
    List<Post> findPublishedByProjectId(long projectId);

    @Query("SELECT p FROM Post p WHERE p.moderationStatus = 'UNVERIFIED'")
    List<Post> findUnverifiedPosts();
}
