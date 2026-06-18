package io.therapistai.security.application;

import io.therapistai.auth.UserIdentity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class TherapistUserPrincipal implements UserDetails, UserIdentity {

    private final UUID userId;
    private final String username;
    private final String passwordHash;
    private final boolean enabled;
    private final boolean onboardingCompleted;
    private final String email;
    private final String fullName;
    private final List<GrantedAuthority> authorities;

    public TherapistUserPrincipal(
            UUID userId,
            String username,
            String passwordHash,
            boolean enabled,
            boolean onboardingCompleted,
            String email,
            String fullName,
            List<GrantedAuthority> authorities
    ) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        this.enabled = enabled;
        this.onboardingCompleted = onboardingCompleted;
        this.email = email;
        this.fullName = fullName;
        this.authorities = authorities != null ? List.copyOf(authorities) : List.of();
    }

    @Override
    public UUID userId() {
        return userId;
    }

    public String email() {
        return email;
    }

    public String fullName() {
        return fullName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static TherapistUserPrincipal ofUserRole(
            UUID userId,
            String username,
            String passwordHash,
            boolean enabled,
            boolean onboardingCompleted,
            String email,
            String fullName
    ) {
        return new TherapistUserPrincipal(
                userId,
                username,
                passwordHash,
                enabled,
                onboardingCompleted,
                email,
                fullName,
                List.of((GrantedAuthority) () -> "ROLE_USER")
        );
    }
}