package com.rev.app.service;

import com.rev.app.entity.Follow;
import com.rev.app.entity.User;
import com.rev.app.repository.FollowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FollowService followService;

    @Test
    void follow_success() {
        User follower = new User();
        follower.setId(1L);
        follower.setUsername("alice");
        User followed = new User();
        followed.setId(2L);
        followed.setUsername("bob");
        followed.setRole(User.UserRole.CREATOR);
        Follow follow = new Follow(follower, followed);

        when(followRepository.existsByFollowerIdAndFollowedId(1L, 2L)).thenReturn(false);
        when(followRepository.save(any(Follow.class))).thenReturn(follow);

        Follow saved = followService.follow(follower, followed);

        assertEquals(follower, saved.getFollower());
        assertEquals(followed, saved.getFollowed());
    }

    @Test
    void follow_duplicate_throws() {
        User follower = new User();
        follower.setId(1L);
        User followed = new User();
        followed.setId(2L);
        followed.setRole(User.UserRole.CREATOR);
        when(followRepository.existsByFollowerIdAndFollowedId(1L, 2L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> followService.follow(follower, followed));
    }
}
