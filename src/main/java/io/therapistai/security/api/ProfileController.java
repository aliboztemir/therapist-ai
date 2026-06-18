package io.therapistai.security.api;

import io.therapistai.auth.UserIdentity;
import io.therapistai.security.application.UserProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserProfileService userProfileService;

    public ProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public String profilePage(
            Authentication authentication,
            Model model
    ) {
        UUID userId = requireUserId(authentication);

        model.addAttribute("profile", userProfileService.loadProfile(userId));
        model.addAttribute("profileUpdated", false);
        model.addAttribute("passwordUpdated", false);

        return "profile";
    }

    @PostMapping
    public String updateProfile(
            Authentication authentication,
            @Valid @ModelAttribute ProfileUpdateForm form,
            BindingResult bindingResult,
            Model model
    ) {
        UUID userId = requireUserId(authentication);

        if (bindingResult.hasErrors()) {
            model.addAttribute("profileUpdated", false);
            model.addAttribute("passwordUpdated", false);
            model.addAttribute("profileError", firstError(bindingResult));
            model.addAttribute("profile", userProfileService.loadProfile(userId));
            return "profile";
        }

        try {
            userProfileService.updateProfile(
                    userId,
                    form.fullName(),
                    form.preferredName(),
                    form.birthDate(),
                    form.gender(),
                    form.country(),
                    form.city(),
                    form.preferredLanguage(),
                    form.timezone()
            );

            model.addAttribute("profileUpdated", true);
            model.addAttribute("passwordUpdated", false);
            model.addAttribute("profile", userProfileService.loadProfile(userId));

        } catch (Exception ex) {
            model.addAttribute("profileUpdated", false);
            model.addAttribute("passwordUpdated", false);
            model.addAttribute("profileError", safeMessage(ex));
            model.addAttribute("profile", userProfileService.loadProfile(userId));
        }

        return "profile";
    }

    @PostMapping("/password")
    public String changePassword(
            Authentication authentication,
            @Valid @ModelAttribute PasswordChangeForm form,
            BindingResult bindingResult,
            Model model
    ) {
        UUID userId = requireUserId(authentication);

        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordUpdated", false);
            model.addAttribute("profileUpdated", false);
            model.addAttribute("passwordError", firstError(bindingResult));
            model.addAttribute("profile", userProfileService.loadProfile(userId));
            return "profile";
        }

        try {
            if (!form.newPassword().equals(form.confirmNewPassword())) {
                throw new IllegalArgumentException("New passwords do not match.");
            }

            userProfileService.changePassword(
                    userId,
                    form.currentPassword(),
                    form.newPassword()
            );

            model.addAttribute("passwordUpdated", true);
            model.addAttribute("profileUpdated", false);
            model.addAttribute("profile", userProfileService.loadProfile(userId));

        } catch (Exception ex) {
            model.addAttribute("passwordUpdated", false);
            model.addAttribute("profileUpdated", false);
            model.addAttribute("passwordError", safeMessage(ex));
            model.addAttribute("profile", userProfileService.loadProfile(userId));
        }

        return "profile";
    }

    private static UUID requireUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserIdentity identity) {
            return identity.userId();
        }

        throw new IllegalStateException("Authenticated principal does not expose userId.");
    }

    private static String firstError(BindingResult bindingResult) {
        if (bindingResult.getFieldError() != null) {
            return bindingResult.getFieldError().getDefaultMessage();
        }

        if (bindingResult.getGlobalError() != null) {
            return bindingResult.getGlobalError().getDefaultMessage();
        }

        return "Validation error.";
    }

    private static String safeMessage(Exception ex) {
        if (ex.getMessage() == null || ex.getMessage().isBlank()) {
            return "Operation failed.";
        }

        return ex.getMessage();
    }

    public record ProfileUpdateForm(
            @NotBlank(message = "Full name is required.")
            @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters.")
            String fullName,

            @Size(max = 100, message = "Preferred name must be at most 100 characters.")
            String preferredName,

            String birthDate,

            @Size(max = 30, message = "Gender must be at most 30 characters.")
            String gender,

            @Size(max = 100, message = "Country must be at most 100 characters.")
            String country,

            @Size(max = 100, message = "City must be at most 100 characters.")
            String city,

            @Size(max = 10, message = "Preferred language must be at most 10 characters.")
            String preferredLanguage,

            @Size(max = 80, message = "Timezone must be at most 80 characters.")
            String timezone
    ) {
    }

    public record PasswordChangeForm(
            @NotBlank(message = "Current password is required.")
            String currentPassword,

            @NotBlank(message = "New password is required.")
            @Size(min = 8, max = 72, message = "New password must be between 8 and 72 characters.")
            String newPassword,

            @NotBlank(message = "Confirm new password is required.")
            String confirmNewPassword
    ) {
    }
}