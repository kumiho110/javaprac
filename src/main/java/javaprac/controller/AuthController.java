package javaprac.controller;

import jakarta.servlet.http.HttpServletRequest;
import javaprac.service.AuthNavigationService;
import javaprac.service.AuthService;
import javaprac.service.AuthenticationRefreshService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;
    private final AuthNavigationService authNavigationService;
    private final AuthenticationRefreshService authenticationRefreshService;

    public AuthController(AuthService authService,
                          AuthNavigationService authNavigationService,
                          AuthenticationRefreshService authenticationRefreshService) {
        this.authService = authService;
        this.authNavigationService = authNavigationService;
        this.authenticationRefreshService = authenticationRefreshService;
    }

    @GetMapping("/login")
    public String login(
            @RequestParam(name = "continue", required = false) String continueUrl,
            Authentication authentication
    ) {
        if (authNavigationService.isAuthenticated(authentication)) {
            return authNavigationService.redirectAuthenticatedUser(authentication, continueUrl);
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(
            @RequestParam(name = "continue", required = false) String continueUrl,
            Authentication authentication
    ) {
        if (authNavigationService.isAuthenticated(authentication)) {
            return authNavigationService.redirectAuthenticatedUser(authentication, continueUrl);
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String password2,
            @RequestParam String fullName,
            @RequestParam(name = "continue", required = false) String continueUrl,
            Model model,
            HttpServletRequest request
    ) {
        try {
            var user = authService.register(email, fullName, password, password2);
            authenticationRefreshService.loginUser(user.getEmail(), request);
            return authNavigationService.redirectAfterRegistration(continueUrl);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }
}