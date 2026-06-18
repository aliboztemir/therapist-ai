package io.therapistai.security.api;

import io.therapistai.auth.UserIdentity;
import io.therapistai.security.application.UserProfileService;
import io.therapistai.security.domain.UserProfile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Controller
public class HomeController {

    private final UserProfileService userProfileService;

    public HomeController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping({"/", "/index.html"})
    public String home(
            Authentication authentication,
            Model model
    ) {
        UUID userId = requireUserId(authentication);
        UserProfile profile = userProfileService.loadProfile(userId);

        model.addAttribute("user", profile);

        return "index";
    }

    private static UUID requireUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserIdentity identity) {
            return identity.userId();
        }

        throw new IllegalStateException("Authenticated principal does not expose userId.");
    }
}