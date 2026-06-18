package io.therapistai.security.application;

import io.therapistai.security.domain.AppUser;
import io.therapistai.security.domain.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DefaultConsentService implements ConsentService {

    private static final Logger log = LoggerFactory.getLogger(DefaultConsentService.class);

    private final AppUserRepository userRepository;

    public DefaultConsentService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isConsentGiven(UUID userId) {
        return userRepository.findByUserUuid(userId)
                .map(AppUser::isOnboardingCompleted)
                .orElse(false);
    }

    @Override
    @Transactional
    public void acceptConsent(UUID userId) {

        AppUser user = userRepository.findByUserUuid(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (user.isOnboardingCompleted()) {
            return;
        }

        user.setOnboardingCompleted(true);

        userRepository.save(user);

        log.info("consent.accepted userId={}", userId);
    }
}