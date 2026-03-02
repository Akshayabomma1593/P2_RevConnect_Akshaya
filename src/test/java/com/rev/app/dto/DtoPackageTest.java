package com.rev.app.dto;

import com.rev.app.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoPackageTest {

    @Test
    void registerDto_gettersAndSettersWork() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");
        dto.setPassword("secret");
        dto.setFullName("Alice");
        dto.setRole(User.UserRole.BUSINESS);

        assertEquals("alice", dto.getUsername());
        assertEquals("alice@example.com", dto.getEmail());
        assertEquals("secret", dto.getPassword());
        assertEquals("Alice", dto.getFullName());
        assertEquals(User.UserRole.BUSINESS, dto.getRole());
    }

    @Test
    void otherDtos_gettersAndSettersWork() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsernameOrEmail("u");
        loginDTO.setPassword("p");
        assertEquals("u", loginDTO.getUsernameOrEmail());

        NotificationPreferenceDTO pref = new NotificationPreferenceDTO();
        pref.setPostLikes(true);
        pref.setPostComments(false);
        assertTrue(pref.isPostLikes());
        assertFalse(pref.isPostComments());
    }
}
