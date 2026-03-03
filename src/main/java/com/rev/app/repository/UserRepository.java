package com.rev.app.repository;

import com.rev.app.entity.User;
import com.rev.app.dto.UserSummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>,
                JpaSpecificationExecutor<User> {

        Optional<User> findByUsername(String username);

        Optional<User> findByEmail(String email);

        Optional<User> findByUsernameOrEmail(String username, String email);

        boolean existsByUsername(String username);

        boolean existsByEmail(String email);

        @Query(value = "SELECT * FROM users u WHERE (u.is_active = true OR u.is_active IS NULL) AND (" +
                        "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(COALESCE(u.full_name, '')) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))",
                        nativeQuery = true)
        List<User> searchActiveUsers(@Param("query") String query);

        @Query(value = "SELECT * FROM users u WHERE " +
                        "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(COALESCE(u.full_name, '')) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                        "LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))",
                        nativeQuery = true)
        List<User> searchUsersIncludingInactive(@Param("query") String query);

        // Projection: lightweight user summaries for search results
        @Query("SELECT u FROM User u WHERE " +
                        "(LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
                        "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
                        "AND u.active = true")
        List<UserSummaryProjection> searchUsers(@Param("query") String query);

        // Find users by role
        List<UserSummaryProjection> findByRole(User.UserRole role);

        // Count followers of a user
        @Query("SELECT COUNT(f) FROM Follow f WHERE f.followed.id = :userId")
        long countFollowers(@Param("userId") Long userId);

        // Count following
        @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId")
        long countFollowing(@Param("userId") Long userId);
}
