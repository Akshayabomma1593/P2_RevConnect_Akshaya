package com.rev.app.service;

import com.rev.app.entity.PasswordResetToken;
import com.rev.app.entity.User;
import com.rev.app.repository.PasswordResetTokenRepository;
import com.rev.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    void createResetToken_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setActive(true);
        when(userRepository.findByUsernameOrEmail("alice", "alice")).thenReturn(Optional.of(user));

        String token = passwordResetService.createResetToken("alice");

        assertNotNull(token);
        assertFalse(token.isBlank());
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void createResetToken_unknownUser_throws() {
        when(userRepository.findByUsernameOrEmail("ghost", "ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> passwordResetService.createResetToken("ghost"));
    }

    @Test
    void resetPassword_success() {
        User user = new User();
        user.setId(7L);
        user.setUsername("alice");

        PasswordResetToken token = new PasswordResetToken(user, "hash", LocalDateTime.now().plusMinutes(5));
        when(passwordResetTokenRepository.findByTokenHashAndUsedFalseAndExpiresAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(token));
        when(passwordEncoder.encode("new-secret")).thenReturn("encoded-secret");

        passwordResetService.resetPassword("plain-token", "new-secret");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded-secret", userCaptor.getValue().getPassword());
        verify(passwordResetTokenRepository).save(token);
    }
}
