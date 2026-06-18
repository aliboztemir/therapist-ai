package io.therapistai.security.infrastructure;

import io.therapistai.security.domain.AppUser;

class AppUserMapper {

    private AppUserMapper() {
    }

    static AppUser toDomain(AppUserEntity e) {
        return new AppUser(
                e.getUserUuid(),
                e.getUsername(),
                e.getEmail(),
                e.getPassword(),
                e.getFullName(),
                e.getPreferredName(),
                e.getBirthDate(),
                e.getGender(),
                e.getCountry(),
                e.getCity(),
                e.getPreferredLanguage(),
                e.getTimezone(),
                e.isEnabled(),
                e.isOnboardingCompleted(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    static AppUserEntity toEntity(AppUser user) {
        return new AppUserEntity(
                user.getUserUuid(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                user.getPreferredName(),
                user.getBirthDate(),
                user.getGender(),
                user.getCountry(),
                user.getCity(),
                user.getPreferredLanguage(),
                user.getTimezone(),
                user.isEnabled(),
                user.isOnboardingCompleted(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}