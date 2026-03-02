package com.rev.app.controller;

import com.rev.app.dto.RegisterDTO;
import com.rev.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthControllerTest {

    @Test
    void home_authenticated_redirectsToFeed() {
        AuthController controller = new AuthController(Mockito.mock(UserService.class));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "alice", "x", AuthorityUtils.createAuthorityList("ROLE_USER"));

        String view = controller.home(auth);

        assertEquals("redirect:/feed", view);
    }

    @Test
    void register_success_redirectsToLogin() {
        UserService userService = Mockito.mock(UserService.class);
        AuthController controller = new AuthController(userService);
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("alice");
        BindingResult result = new BeanPropertyBindingResult(dto, "registerDTO");

        String view = controller.register(dto, result, new ExtendedModelMap(), new RedirectAttributesModelMap());

        assertEquals("redirect:/login", view);
        Mockito.verify(userService).register(dto);
    }
}
