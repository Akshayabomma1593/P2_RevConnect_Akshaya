package com.rev.app.service;

import com.rev.app.dto.CommentDTO;
import com.rev.app.entity.Comment;
import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import com.rev.app.repository.CommentRepository;
import com.rev.app.repository.LikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InteractionServiceTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private InteractionService interactionService;

    @Test
    void toggleLike_addsLikeWhenNotLiked() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        User author = new User();
        author.setId(2L);
        Post post = new Post();
        post.setId(10L);
        post.setAuthor(author);
        when(likeRepository.existsByUserIdAndPostId(1L, 10L)).thenReturn(false);

        boolean liked = interactionService.toggleLike(post, user);

        assertTrue(liked);
    }

    @Test
    void toggleLike_removesLikeWhenAlreadyLiked() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        Post post = new Post();
        post.setId(10L);
        when(likeRepository.existsByUserIdAndPostId(1L, 10L)).thenReturn(true);

        boolean liked = interactionService.toggleLike(post, user);

        assertFalse(liked);
    }

    @Test
    void addComment_savesComment() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        User author = new User();
        author.setId(2L);
        Post post = new Post();
        post.setId(10L);
        post.setAuthor(author);
        CommentDTO dto = new CommentDTO();
        dto.setContent("nice");
        when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));

        Comment saved = interactionService.addComment(post, user, dto);

        assertTrue(saved.getContent().contains("nice"));
    }
}
