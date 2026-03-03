package com.rev.app.service;

import com.rev.app.entity.PasswordResetToken;
import com.rev.app.entity.User;
import com.rev.app.repository.PasswordResetTokenRepository;
import com.rev.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Transactional
public class PasswordResetService {

    private static final int TOKEN_TTL_MINUTES = 15;
    private static final int TOKEN_BYTES = 32;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String createResetToken(String usernameOrEmail) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank()) {
            throw new IllegalArgumentException("Username or email is required.");
        }

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail.trim(), usernameOrEmail.trim())
                .orElseThrow(() -> new IllegalArgumentException("No account found for the given identifier."));

        if (!user.isActive()) {
            throw new IllegalStateException("Account is deactivated.");
        }

        LocalDateTime now = LocalDateTime.now();
        passwordResetTokenRepository.invalidateActiveTokensForUser(user.getId(), now);

        String rawToken = generateRawToken();
        PasswordResetToken token = new PasswordResetToken(user, hashToken(rawToken), now.plusMinutes(TOKEN_TTL_MINUTES));
        passwordResetTokenRepository.save(token);
        return rawToken;
    }

    public void resetPassword(String rawToken, String newPassword) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Reset token is required.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken token = passwordResetTokenRepository
                .findByTokenHashAndUsedFalseAndExpiresAtAfter(hashToken(rawToken.trim()), now)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token."));

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        token.setUsedAt(now);
        passwordResetTokenRepository.save(token);
        passwordResetTokenRepository.invalidateActiveTokensForUser(user.getId(), now);
    }

    public int getTokenTtlMinutes() {
        return TOKEN_TTL_MINUTES;
    }

    private String generateRawToken() {
        byte[] buffer = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(buffer);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
