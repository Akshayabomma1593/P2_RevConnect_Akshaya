package com.rev.app.rest;

import com.rev.app.dto.ApiUserSummary;
import com.rev.app.entity.User;
import com.rev.app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRestControllerTest {

    private MockMvc mockMvc;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserRestController(userService)).build();
    }

    @Test
    void searchUsers_returnsList() throws Exception {
        ApiUserSummary user = new ApiUserSummary(1L, "alice", "Alice", null, "PERSONAL", "bio");
        Mockito.when(userService.searchUserSummaries("ali")).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users/search").param("q", "ali").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test
    void deleteUser_returnsNoContent() throws Exception {
        User currentUser = new User();
        currentUser.setId(10L);
        currentUser.setUsername("alice");
        Mockito.when(userService.findByUsername("alice")).thenReturn(currentUser);

        mockMvc.perform(delete("/api/users/10").principal(() -> "alice"))
                .andExpect(status().isNoContent());
        Mockito.verify(userService).deleteUser(10L);
    }
}
