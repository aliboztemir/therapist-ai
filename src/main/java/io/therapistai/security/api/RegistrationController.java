package io.therapistai.security.api;

import io.therapistai.security.application.RegisterRequest;
import io.therapistai.security.application.RegistrationUseCase;
import io.therapistai.security.domain.DuplicateEmailException;
import io.therapistai.security.domain.DuplicateUsernameException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    private final RegistrationUseCase registrationUseCase;

    public RegistrationController(RegistrationUseCase registrationUseCase) {
        this.registrationUseCase = registrationUseCase;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String confirmPassword,
            Model model
    ) {
        try {
            registrationUseCase.register(
                    new RegisterRequest(
                            username,
                            fullName,
                            email,
                            password,
                            confirmPassword
                    )
            );

            return "redirect:/login?registered";

        } catch (DuplicateUsernameException e) {
            model.addAttribute("error", "This username is already taken.");
        } catch (DuplicateEmailException | DataIntegrityViolationException e) {
            model.addAttribute("error", "This email address is already registered.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. Please try again.");
        }

        return "register";
    }
}