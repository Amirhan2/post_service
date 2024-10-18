package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("""
            SELECT p FROM Post p
            LEFT JOIN FETCH p.likes
            WHERE p.authorId = :authorId AND p.published = :published AND p.deleted = :deleted
            """)
    List<Post> findByAuthorIdAndPublishedAndDeletedWithLikes(long authorId, boolean published, boolean deleted);

    @Query("""
            SELECT p FROM Post p
            LEFT JOIN FETCH p.likes
            WHERE p.projectId = :projectId AND p.published = :published AND p.deleted = :deleted
            """)
    List<Post> findByProjectIdAndPublishedAndDeletedWithLikes(long projectId, boolean published, boolean deleted);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.isVerify = 'UNCHECKED'")
    List<Post> findAllUncheckedPosts();

    @Query("SELECT p FROM Post p WHERE p.isVerify = 'NOT_VERIFIED'")
    List<Post> findAllNotVerifiedPosts();

    @Query("SELECT p FROM Post p " +
            "WHERE p.published = true AND " +
            "p.deleted = false AND " +
            "p.authorId IN :authors AND " +
            "p.id >= :postId " +
            "ORDER BY p.publishedAt DESC " +
            "LIMIT :countPosts")
    List<Post> findByAuthorsAndLimitAndStartFromPostId(List<Long> authors, int countPosts, long postId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published = true AND " +
            "p.deleted = false AND " +
            "p.authorId IN :authors " +
            "ORDER BY p.publishedAt DESC " +
            "LIMIT :countPosts")
    List<Post> findByAuthorsAndLimit(List<Long> authors, int countPosts);
}
