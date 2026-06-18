package io.therapistai.security.application;

import io.therapistai.security.domain.AppUser;
import io.therapistai.security.domain.AppUserRepository;
import io.therapistai.security.domain.DuplicateEmailException;
import io.therapistai.security.domain.DuplicateUsernameException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class RegistrationUseCase {

    private static final List<String> FIELD_ORDER =
            List.of("username", "fullName", "email", "password", "confirmPassword");

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public RegistrationUseCase(
            AppUserRepository repository,
            PasswordEncoder passwordEncoder,
            Validator validator
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    public void register(RegisterRequest request) {
        RegisterRequest normalized = normalize(request);
        validate(normalized);
        validatePassword(normalized);
        checkUniqueness(normalized);

        AppUser user = new AppUser(
                normalized.username(),
                normalized.fullName(),
                normalized.email(),
                passwordEncoder.encode(normalized.password())
        );

        repository.save(user);
    }

    private RegisterRequest normalize(RegisterRequest request) {
        String username = request.username() == null ? null : request.username().strip().toLowerCase();
        String fullName = request.fullName() == null ? null : request.fullName().strip();
        String email = request.email() == null ? null : request.email().strip().toLowerCase();

        return new RegisterRequest(
                username,
                fullName,
                email,
                request.password(),
                request.confirmPassword()
        );
    }

    private void validate(RegisterRequest request) {
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        if (violations.isEmpty()) {
            return;
        }

        String message = violations.stream()
                .min(Comparator.comparingInt(v -> fieldOrderIndex(v.getPropertyPath().toString())))
                .map(ConstraintViolation::getMessage)
                .orElse("Validation error.");

        throw new IllegalArgumentException(message);
    }

    private int fieldOrderIndex(String fieldName) {
        int index = FIELD_ORDER.indexOf(fieldName);
        return index >= 0 ? index : Integer.MAX_VALUE;
    }

    private void validatePassword(RegisterRequest request) {
        String password = request.password();

        if (!password.matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one letter.");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one number.");
        }

        if (!password.equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
    }

    private void checkUniqueness(RegisterRequest request) {
        if (repository.existsByUsername(request.username())) {
            throw new DuplicateUsernameException(request.username());
        }

        if (repository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }
    }
}