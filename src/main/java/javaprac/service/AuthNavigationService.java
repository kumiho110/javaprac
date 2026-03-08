package javaprac.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthNavigationService {

    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public boolean isSafeLocalTarget(String target) {
        return target != null
                && !target.isBlank()
                && target.startsWith("/")
                && !target.startsWith("//")
                && !target.startsWith("/\\");
    }

    public String defaultRedirect(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_MANAGER".equals(a.getAuthority()));

        return isAdmin ? "redirect:/admin"
                : isManager ? "redirect:/staff/orders"
                : "redirect:/products";
    }

    public String redirectAuthenticatedUser(Authentication authentication, String continueUrl) {
        if (isSafeLocalTarget(continueUrl)) {
            return "redirect:" + continueUrl;
        }
        return defaultRedirect(authentication);
    }

    public String redirectAfterRegistration(String continueUrl) {
        if (isSafeLocalTarget(continueUrl)) {
            return "redirect:" + continueUrl;
        }
        return "redirect:/products";
    }
}