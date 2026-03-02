package com.rev.app.service;

import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.NotificationPreference;
import com.rev.app.entity.User;
import com.rev.app.exception.UserAlreadyExistsException;
import com.rev.app.mapper.UserMapper;
import com.rev.app.repository.NotificationPreferenceRepository;
import com.rev.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void register_success() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");
        User mapped = new User();
        mapped.setUsername("alice");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userMapper.toEntity(dto, passwordEncoder)).thenReturn(mapped);
        when(userRepository.save(mapped)).thenReturn(mapped);
        when(notificationPreferenceRepository.save(any(NotificationPreference.class))).thenReturn(new NotificationPreference());

        User saved = userService.register(dto);

        assertEquals("alice", saved.getUsername());
        verify(notificationPreferenceRepository).save(any(NotificationPreference.class));
    }

    @Test
    void register_duplicateUsername_throws() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("alice");
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(dto));
    }

    @Test
    void findByUsername_success() {
        User user = new User();
        user.setUsername("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("alice");

        assertEquals("alice", result.getUsername());
    }
}
