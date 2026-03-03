package com.rev.app.service;

import com.rev.app.dto.PostDTO;
import com.rev.app.entity.Post;
import com.rev.app.entity.User;
import com.rev.app.exception.AccessDeniedException;
import com.rev.app.mapper.PostMapper;
import com.rev.app.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PostMapper postMapper;
    @InjectMocks
    private PostService postService;

    @Test
    void createPost_withoutImage_success() throws Exception {
        User author = new User();
        author.setUsername("alice");
        PostDTO dto = new PostDTO();
        dto.setContent("hello");
        Post post = new Post();
        post.setContent("hello");

        when(postMapper.toEntity(dto, author)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);

        Post result = postService.createPost(author, dto, null);

        assertEquals("hello", result.getContent());
    }

    @Test
    void createPost_withInvalidImageType_throws() {
        User author = new User();
        author.setUsername("alice");
        author.setId(1L);
        PostDTO dto = new PostDTO();
        dto.setContent("hello");
        Post post = new Post();
        post.setContent("hello");

        MockMultipartFile file = new MockMultipartFile("image", "malicious.svg", "image/svg+xml",
                "<svg></svg>".getBytes());

        when(postMapper.toEntity(dto, author)).thenReturn(post);

        assertThrows(IllegalArgumentException.class, () -> postService.createPost(author, dto, file));
    }

    @Test
    void deletePost_notOwner_throws() {
        User author = new User();
        author.setId(1L);
        Post post = new Post();
        post.setId(10L);
        post.setAuthor(author);
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        assertThrows(AccessDeniedException.class, () -> postService.deletePost(10L, 2L));
    }

    @Test
    void deletePost_owner_success() {
        User author = new User();
        author.setId(1L);
        Post post = new Post();
        post.setId(10L);
        post.setAuthor(author);
        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        postService.deletePost(10L, 1L);

        verify(notificationService).deletePostNotifications(10L);
        verify(postRepository).delete(post);
    }
}
