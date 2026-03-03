package com.rev.app.service;

import com.rev.app.entity.User;
import com.rev.app.repository.CommentRepository;
import com.rev.app.repository.ConnectionRepository;
import com.rev.app.repository.FollowRepository;
import com.rev.app.repository.LikeRepository;
import com.rev.app.repository.PostRepository;
import com.rev.app.repository.PostViewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private FollowService followService;
    @Mock
    private FollowRepository followRepository;
    @Mock
    private UserService userService;
    @Mock
    private ConnectionRepository connectionRepository;
    @Mock
    private PostViewRepository postViewRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getAccountMetrics_returnsExpectedCounts() {
        User user = new User();
        user.setId(1L);
        when(userService.findById(1L)).thenReturn(user);
        when(postRepository.countPublishedPostsByAuthor(1L)).thenReturn(10L);
        when(followService.countFollowers(1L)).thenReturn(20L);
        when(followService.countFollowing(1L)).thenReturn(30L);
        when(connectionRepository.countConnections(user)).thenReturn(40L);
        when(postViewRepository.countByAuthorId(1L)).thenReturn(50L);

        Map<String, Object> metrics = analyticsService.getAccountMetrics(1L);

        assertEquals(10L, metrics.get("totalPosts"));
        assertEquals(20L, metrics.get("totalFollowers"));
        assertEquals(30L, metrics.get("totalFollowing"));
        assertEquals(40L, metrics.get("totalConnections"));
        assertEquals(50L, metrics.get("totalReach"));
    }
}
