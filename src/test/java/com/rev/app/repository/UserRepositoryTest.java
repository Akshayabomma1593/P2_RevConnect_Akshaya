package com.rev.app.repository;

import com.rev.app.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb_min;DB_CLOSE_DELAY=-1;MODE=LEGACY",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_returnsSavedUser() {
        User user = new User();
        user.setUsername("repo_user");
        user.setEmail("repo_user@example.com");
        user.setPassword("pw");
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("repo_user");

        assertTrue(found.isPresent());
        assertEquals("repo_user@example.com", found.get().getEmail());
    }

    @Test
    void searchUsers_returnsOnlyActiveUsers() {
        User active = new User();
        active.setUsername("alice_active");
        active.setEmail("alice_active@example.com");
        active.setPassword("pw");
        active.setActive(true);
        userRepository.save(active);

        User inactive = new User();
        inactive.setUsername("alice_inactive");
        inactive.setEmail("alice_inactive@example.com");
        inactive.setPassword("pw");
        inactive.setActive(false);
        userRepository.save(inactive);

        assertEquals(1, userRepository.searchUsers("alice").size());
    }
}
