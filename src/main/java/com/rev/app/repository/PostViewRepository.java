package com.rev.app.repository;

import com.rev.app.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, Long> {

    boolean existsByPostIdAndViewerId(Long postId, Long viewerId);

    long countByPostId(Long postId);

    @Query("SELECT COUNT(pv) FROM PostView pv WHERE pv.post.author.id = :authorId")
    long countByAuthorId(@Param("authorId") Long authorId);
}
