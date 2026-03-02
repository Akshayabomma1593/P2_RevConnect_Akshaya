package com.rev.app.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenUtilTest {

    private final JwtTokenUtil tokenUtil = new JwtTokenUtil();

    @Test
    void generateAndValidateToken_success() {
        UserDetails user = new User("alice", "x", AuthorityUtils.NO_AUTHORITIES);
        String token = tokenUtil.generateToken(user);

        assertEquals("alice", tokenUtil.getUsernameFromToken(token));
        assertTrue(tokenUtil.validateToken(token, user));
    }

    @Test
    void validateToken_withDifferentUser_false() {
        UserDetails owner = new User("owner", "x", AuthorityUtils.NO_AUTHORITIES);
        UserDetails other = new User("other", "x", AuthorityUtils.NO_AUTHORITIES);
        String token = tokenUtil.generateToken(owner);

        assertFalse(tokenUtil.validateToken(token, other));
    }
}
