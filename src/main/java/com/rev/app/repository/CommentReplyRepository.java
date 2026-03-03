package com.rev.app.repository;

import com.rev.app.entity.CommentReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentReplyRepository extends JpaRepository<CommentReply, Long> {
    List<CommentReply> findByCommentPostIdOrderByCreatedAtAsc(Long postId);
}
