package io.therapistai.security.infrastructure;

import io.therapistai.security.domain.AppUser;
import io.therapistai.security.domain.AppUserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
class JpaAppUserRepositoryAdapter implements AppUserRepository {

    private final SpringDataAppUserRepository jpa;

    JpaAppUserRepositoryAdapter(SpringDataAppUserRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return jpa.findByUsername(username)
                .map(AppUserMapper::toDomain);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return jpa.findByEmail(email)
                .map(AppUserMapper::toDomain);
    }

    @Override
    public Optional<AppUser> findByUserUuid(UUID userUuid) {
        return jpa.findByUserUuid(userUuid)
                .map(AppUserMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpa.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    @Transactional
    public void save(AppUser user) {
        if (user.getUserUuid() == null) {
            jpa.save(AppUserMapper.toEntity(user));
            return;
        }

        AppUserEntity entity = jpa.findByUserUuid(user.getUserUuid())
                .orElseGet(() -> AppUserMapper.toEntity(user));

        updateEntity(entity, user);

        jpa.save(entity);
    }

    private static void updateEntity(
            AppUserEntity entity,
            AppUser user
    ) {
        entity.setPasswordHash(user.getPassword());
        entity.setFullName(user.getFullName());
        entity.setPreferredName(user.getPreferredName());
        entity.setBirthDate(user.getBirthDate());
        entity.setGender(user.getGender());
        entity.setCountry(user.getCountry());
        entity.setCity(user.getCity());
        entity.setPreferredLanguage(user.getPreferredLanguage());
        entity.setTimezone(user.getTimezone());
        entity.setEnabled(user.isEnabled());
        entity.setOnboardingCompleted(user.isOnboardingCompleted());
    }
}