package io.therapistai.security.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link AppUserEntity}.
 * Package-private — accessed only through {@link JpaAppUserRepositoryAdapter}.
 */
interface SpringDataAppUserRepository extends JpaRepository<AppUserEntity, Long> {

    Optional<AppUserEntity> findByUsername(String username);

    Optional<AppUserEntity> findByEmail(String email);

    Optional<AppUserEntity> findByUserUuid(UUID userUuid);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

