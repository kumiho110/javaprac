package javaprac.service;

import jakarta.servlet.http.HttpServletRequest;
import javaprac.model.AppRole;
import javaprac.model.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Collections;

@Service
public class CatalogViewService {

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    public CatalogViewService(CartService cartService,
                              CurrentUserService currentUserService) {
        this.cartService = cartService;
        this.currentUserService = currentUserService;
    }

    public void fillCartInfo(Model model) {
        AppUser user = currentUserService.currentUserOrNull();

        boolean canUseCart = user != null && user.getRole() == AppRole.USER;
        model.addAttribute("canUseCart", canUseCart);

        if (!canUseCart) {
            model.addAttribute("cartQtyByProductId", Collections.emptyMap());
            model.addAttribute("cartDistinctItemCount", 0);
            return;
        }

        model.addAttribute("cartQtyByProductId", cartService.getCartQtyByProductIdStr(user.getId()));
        model.addAttribute("cartDistinctItemCount", cartService.getCartDistinctItemCount(user.getId()));
    }

    public void fillContinueUrl(Model model, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String qs = request.getQueryString();
        String continueUrl = (qs == null || qs.isBlank()) ? uri : (uri + "?" + qs);
        model.addAttribute("continueUrl", continueUrl);
    }
}