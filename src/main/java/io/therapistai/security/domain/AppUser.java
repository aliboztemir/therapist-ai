package io.therapistai.security.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class AppUser {

    private final UUID userUuid;
    private final String username;
    private final String email;
    private final LocalDateTime createdAt;

    private String password;
    private String fullName;
    private String preferredName;
    private LocalDate birthDate;
    private String gender;
    private String country;
    private String city;
    private String preferredLanguage;
    private String timezone;
    private boolean enabled;
    private boolean onboardingCompleted;
    private LocalDateTime updatedAt;

    public AppUser(
            UUID userUuid,
            String username,
            String email,
            String password,
            String fullName,
            String preferredName,
            LocalDate birthDate,
            String gender,
            String country,
            String city,
            String preferredLanguage,
            String timezone,
            boolean enabled,
            boolean onboardingCompleted,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.userUuid = userUuid;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.preferredName = preferredName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.country = country;
        this.city = city;
        this.preferredLanguage = preferredLanguage;
        this.timezone = timezone;
        this.enabled = enabled;
        this.onboardingCompleted = onboardingCompleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public AppUser(
            String username,
            String fullName,
            String email,
            String password
    ) {
        this(
                null,
                username,
                email,
                password,
                fullName,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true,
                false,
                null,
                null
        );
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public String getTimezone() {
        return timezone;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void updateProfile(
            String fullName,
            String preferredName,
            LocalDate birthDate,
            String gender,
            String country,
            String city,
            String preferredLanguage,
            String timezone
    ) {
        this.fullName = fullName;
        this.preferredName = preferredName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.country = country;
        this.city = city;
        this.preferredLanguage = preferredLanguage;
        this.timezone = timezone;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }

    public AuthenticatedUser toAuthenticatedUser() {
        return new AuthenticatedUser(
                userUuid,
                username,
                email,
                fullName
        );
    }

    public UserProfile toUserProfile() {
        return new UserProfile(
                userUuid,
                username,
                email,
                fullName,
                preferredName,
                birthDate,
                gender,
                country,
                city,
                preferredLanguage,
                timezone,
                onboardingCompleted
        );
    }
}