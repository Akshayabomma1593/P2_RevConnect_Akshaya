package com.rev.app.mapper;

import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toEntity_mapsFields() {
        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(encoder.encode("plain")).thenReturn("encoded");
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");
        dto.setPassword("plain");
        dto.setRole(User.UserRole.CREATOR);

        User result = mapper.toEntity(dto, encoder);

        assertEquals("alice", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertEquals(User.UserRole.CREATOR, result.getRole());
    }

    @Test
    void toEntity_defaultsRoleToPersonal() {
        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
        Mockito.when(encoder.encode("plain")).thenReturn("encoded");
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("bob");
        dto.setEmail("bob@example.com");
        dto.setPassword("plain");
        dto.setRole(null);

        User result = mapper.toEntity(dto, encoder);

        assertEquals(User.UserRole.PERSONAL, result.getRole());
    }
}
