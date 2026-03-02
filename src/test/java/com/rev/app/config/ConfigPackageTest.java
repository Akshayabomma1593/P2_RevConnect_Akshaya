package com.rev.app.config;

import com.rev.app.entity.User;
import com.rev.app.repository.UserRepository;
import com.rev.app.security.JwtRequestFilter;
import com.rev.app.security.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ConfigPackageTest {

    @Test
    void customUserDetailsService_loadsActiveUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        User user = new User();
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("encoded");
        user.setRole(User.UserRole.PERSONAL);
        user.setActive(true);
        when(userRepository.findByUsernameOrEmail("alice", "alice")).thenReturn(Optional.of(user));
        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

        UserDetails userDetails = service.loadUserByUsername("alice");

        assertNotNull(userDetails);
        assertEquals("alice", userDetails.getUsername());
    }

    @Test
    void customUserDetailsService_inactiveUserThrows() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        User user = new User();
        user.setUsername("bob");
        user.setEmail("bob@example.com");
        user.setPassword("encoded");
        user.setRole(User.UserRole.PERSONAL);
        user.setActive(false);
        when(userRepository.findByUsernameOrEmail("bob", "bob")).thenReturn(Optional.of(user));
        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("bob"));
    }

    @Test
    void webSecurityConfig_createsBeans() {
        CustomUserDetailsService uds = Mockito.mock(CustomUserDetailsService.class);
        JwtRequestFilter filter = new JwtRequestFilter(uds, Mockito.mock(JwtTokenUtil.class));
        WebSecurityConfig config = new WebSecurityConfig(uds, filter);

        assertNotNull(config.passwordEncoder());
        DaoAuthenticationProvider provider = config.authenticationProvider();
        assertNotNull(provider);
    }

    @Test
    void webConfig_registersUploadHandlers() {
        WebConfig webConfig = new WebConfig();
        StaticWebApplicationContext context = new StaticWebApplicationContext();
        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(context, new MockServletContext());

        webConfig.addResourceHandlers(registry);

        assertTrue(registry.hasMappingForPattern("/uploads/**"));
        assertTrue(registry.hasMappingForPattern("/uploads/profile-pictures/**"));
        assertTrue(registry.hasMappingForPattern("/uploads/post-images/**"));
    }
}
