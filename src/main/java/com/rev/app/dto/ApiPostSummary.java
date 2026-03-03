package com.rev.app.dto;

import com.rev.app.entity.Post;

import java.time.LocalDateTime;

public record ApiPostSummary(
        Long id,
        String content,
        String hashtags,
        String postType,
        String imageUrl,
        LocalDateTime createdAt,
        AuthorSummary author) {

    public record AuthorSummary(
            Long id,
            String username,
            String fullName,
            String profilePicture) {
    }

    public static ApiPostSummary fromEntity(Post post) {
        return new ApiPostSummary(
                post.getId(),
                post.getContent(),
                post.getHashtags(),
                post.getPostType() != null ? post.getPostType().name() : null,
                post.getImageUrl(),
                post.getCreatedAt(),
                new AuthorSummary(
                        post.getAuthor().getId(),
                        post.getAuthor().getUsername(),
                        post.getAuthor().getFullName(),
                        post.getAuthor().getProfilePicture()));
    }
}
