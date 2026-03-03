package com.rev.app.dto;

public record ApiUserSummary(
        Long id,
        String username,
        String fullName,
        String profilePicture,
        String role,
        String bio) {

    public static ApiUserSummary fromProjection(UserSummaryProjection projection) {
        return new ApiUserSummary(
                projection.getId(),
                projection.getUsername(),
                projection.getFullName(),
                projection.getProfilePicture(),
                projection.getRole(),
                projection.getBio());
    }
}
