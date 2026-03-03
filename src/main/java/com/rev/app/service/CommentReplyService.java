package com.rev.app.service;

import com.rev.app.entity.Comment;
import com.rev.app.entity.CommentReply;
import com.rev.app.entity.User;
import com.rev.app.exception.AccessDeniedException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.repository.CommentReplyRepository;
import com.rev.app.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentReplyService {

    private final CommentRepository commentRepository;
    private final CommentReplyRepository commentReplyRepository;

    public CommentReplyService(CommentRepository commentRepository,
            CommentReplyRepository commentReplyRepository) {
        this.commentRepository = commentRepository;
        this.commentReplyRepository = commentReplyRepository;
    }

    public CommentReply addReply(Long commentId, User responder, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));
        boolean isPostOwner = comment.getPost().getAuthor().getId().equals(responder.getId());
        boolean isBusinessOrCreator = responder.getRole() != User.UserRole.PERSONAL;
        if (!isPostOwner || !isBusinessOrCreator) {
            throw new AccessDeniedException("Only creator/business post owner can reply.");
        }
        CommentReply reply = new CommentReply(comment, responder, content);
        return commentReplyRepository.save(reply);
    }

    @Transactional(readOnly = true)
    public List<CommentReply> getRepliesForPost(Long postId) {
        return commentReplyRepository.findByCommentPostIdOrderByCreatedAtAsc(postId);
    }
}
