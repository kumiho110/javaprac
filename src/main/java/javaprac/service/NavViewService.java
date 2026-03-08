package javaprac.service;

import jakarta.servlet.http.HttpServletRequest;
import javaprac.model.AppRole;
import javaprac.model.AppUser;
import org.springframework.stereotype.Service;

@Service
public class NavViewService {

    private final CurrentUserService currentUserService;
    private final CartService cartService;

    public NavViewService(CurrentUserService currentUserService, CartService cartService) {
        this.currentUserService = currentUserService;
        this.cartService = cartService;
    }

    public void fillNav(HttpServletRequest request) {
        AppUser user = currentUserService.currentUserOrNull();

        request.setAttribute("navUser", user);
        request.setAttribute("navAuthenticated", user != null);
        request.setAttribute("navIsAdmin", user != null && user.getRole() == AppRole.ADMIN);
        request.setAttribute("navIsManager", user != null && user.getRole() == AppRole.MANAGER);
        request.setAttribute("navIsUser", user != null && user.getRole() == AppRole.USER);

        if (user != null && user.getRole() == AppRole.USER) {
            request.setAttribute("navCartDistinctItemCount", cartService.getCartDistinctItemCount(user.getId()));
        } else {
            request.setAttribute("navCartDistinctItemCount", 0);
        }

        String uri = request.getRequestURI();
        String qs = request.getQueryString();
        String continueUrl = (qs == null || qs.isBlank()) ? uri : (uri + "?" + qs);

        request.setAttribute("navContinueUrl", continueUrl);
    }
}