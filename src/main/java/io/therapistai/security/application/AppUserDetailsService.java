package io.therapistai.security.application;

import io.therapistai.security.domain.AppUser;
import io.therapistai.security.domain.AppUserRepository;
import io.therapistai.security.domain.AuthenticatedUser;
import io.therapistai.security.domain.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class AppUserDetailsService implements UserDetailsService, UserProfileService {

    private static final Logger log =
            LoggerFactory.getLogger(AppUserDetailsService.class);

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AppUserDetailsService(
            AppUserRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(
            String identifier
    ) throws UsernameNotFoundException {
        AppUser user = findByIdentifier(identifier);

        if (user.getUserUuid() == null) {
            throw new UsernameNotFoundException("User has no UUID assigned.");
        }

        return TherapistUserPrincipal.ofUserRole(
                user.getUserUuid(),
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                user.getEmail(),
                resolveDisplayName(user)
        );
    }

    public AuthenticatedUser resolveAuthenticatedUser(UserDetails principal) {
        if (principal instanceof TherapistUserPrincipal p) {
            return new AuthenticatedUser(
                    p.userId(),
                    p.getUsername(),
                    p.email(),
                    p.fullName()
            );
        }

        return findByIdentifier(principal.getUsername()).toAuthenticatedUser();
    }

    @Override
    public UserProfile loadProfile(UUID userId) {
        return repository.findByUserUuid(userId)
                .map(AppUser::toUserProfile)
                .orElseGet(() -> {
                    log.debug("user.profile.not.found userId={}", userId);
                    return fallbackProfile(userId);
                });
    }

    @Override
    @Transactional
    public void updateProfile(
            UUID userId,
            String fullName,
            String preferredName,
            String birthDate,
            String gender,
            String country,
            String city,
            String preferredLanguage,
            String timezone
    ) {
        AppUser user = repository.findByUserUuid(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.updateProfile(
                normalizeRequired(fullName, "fullName"),
                normalizeNullable(preferredName),
                parseNullableDate(birthDate),
                normalizeNullable(gender),
                normalizeNullable(country),
                normalizeNullable(city),
                normalizeNullable(preferredLanguage),
                normalizeNullable(timezone)
        );

        repository.save(user);

        log.info("user.profile.updated userId={}", userId);
    }

    @Override
    @Transactional
    public void changePassword(
            UUID userId,
            String currentPassword,
            String newPassword
    ) {
        AppUser user = repository.findByUserUuid(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (currentPassword == null
                || !passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        repository.save(user);

        log.info("user.password.changed userId={}", userId);
    }

    private AppUser findByIdentifier(String identifier) {
        String normalized = normalizeIdentifier(identifier);

        return normalized.contains("@")
                ? repository.findByEmail(normalized)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + normalized))
                : repository.findByUsername(normalized)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + normalized));
    }

    private String normalizeIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new UsernameNotFoundException("User identifier is empty.");
        }

        return identifier.strip().toLowerCase();
    }

    private static String normalizeRequired(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        return value.strip();
    }

    private static String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.strip();
    }

    private static LocalDate parseNullableDate(String value) {
        String normalized = normalizeNullable(value);

        if (normalized == null) {
            return null;
        }

        return LocalDate.parse(normalized);
    }

    private static String resolveDisplayName(AppUser user) {
        if (user.getPreferredName() != null && !user.getPreferredName().isBlank()) {
            return user.getPreferredName();
        }

        return user.getFullName();
    }

    private static UserProfile fallbackProfile(UUID userId) {
        return new UserProfile(
                userId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
    }
}