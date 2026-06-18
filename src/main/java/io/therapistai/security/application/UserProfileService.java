package io.therapistai.security.application;

import io.therapistai.security.domain.UserProfile;

import java.util.UUID;

public interface UserProfileService {

    UserProfile loadProfile(UUID userId);

    void updateProfile(
            UUID userId,
            String fullName,
            String preferredName,
            String birthDate,
            String gender,
            String country,
            String city,
            String preferredLanguage,
            String timezone
    );

    void changePassword(
            UUID userId,
            String currentPassword,
            String newPassword
    );
}