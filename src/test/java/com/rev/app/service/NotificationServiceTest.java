package com.rev.app.service;

import com.rev.app.entity.Notification;
import com.rev.app.entity.NotificationPreference;
import com.rev.app.entity.User;
import com.rev.app.repository.NotificationPreferenceRepository;
import com.rev.app.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private NotificationPreferenceRepository preferenceRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void notifyPostLiked_savesWhenEnabled() {
        User recipient = new User();
        recipient.setId(1L);
        recipient.setUsername("owner");
        User actor = new User();
        actor.setId(2L);
        actor.setUsername("liker");
        when(preferenceRepository.findByUserId(1L)).thenReturn(Optional.of(new NotificationPreference(recipient)));

        notificationService.notifyPostLiked(recipient, actor, 10L);

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void notifyPostLiked_skipsSelfNotification() {
        User user = new User();
        user.setId(1L);
        user.setUsername("same");

        notificationService.notifyPostLiked(user, user, 10L);

        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    void getUnreadCount_delegatesToRepository() {
        when(notificationRepository.countByRecipientIdAndReadFalse(1L)).thenReturn(3L);

        long count = notificationService.getUnreadCount(1L);

        assertEquals(3L, count);
    }
}
