package io.therapistai.security.infrastructure;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "app_users")
class AppUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "user_uuid",
            nullable = false,
            unique = true,
            updatable = false
    )
    private UUID userUuid;

    @Column(nullable = false, unique = true, length = 255)
    private String username;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "preferred_name", length = 100)
    private String preferredName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 30)
    private String gender;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String city;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage;

    @Column(length = 80)
    private String timezone;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "onboarding_completed", nullable = false)
    private boolean onboardingCompleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    AppUserEntity(
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

    @PrePersist
    void prePersist() {
        if (userUuid == null) {
            userUuid = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    Long getId() {
        return id;
    }

    UUID getUserUuid() {
        return userUuid;
    }

    String getUsername() {
        return username;
    }

    String getEmail() {
        return email;
    }

    String getPassword() {
        return password;
    }

    String getFullName() {
        return fullName;
    }

    String getPreferredName() {
        return preferredName;
    }

    LocalDate getBirthDate() {
        return birthDate;
    }

    String getGender() {
        return gender;
    }

    String getCountry() {
        return country;
    }

    String getCity() {
        return city;
    }

    String getPreferredLanguage() {
        return preferredLanguage;
    }

    String getTimezone() {
        return timezone;
    }

    boolean isEnabled() {
        return enabled;
    }

    boolean isOnboardingCompleted() {
        return onboardingCompleted;
    }

    LocalDateTime getCreatedAt() {
        return createdAt;
    }

    LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    void setPassword(String password) {
        this.password = password;
    }

    void setFullName(String fullName) {
        this.fullName = fullName;
    }

    void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    void setGender(String gender) {
        this.gender = gender;
    }

    void setCountry(String country) {
        this.country = country;
    }

    void setCity(String city) {
        this.city = city;
    }

    void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    void setOnboardingCompleted(boolean onboardingCompleted) {
        this.onboardingCompleted = onboardingCompleted;
    }
}