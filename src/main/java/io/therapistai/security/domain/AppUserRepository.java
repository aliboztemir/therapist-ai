package io.therapistai.security.domain;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository {

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    Optional<AppUser> findByUserUuid(UUID userUuid);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void save(AppUser user);
}