package javaprac.controller;

import jakarta.servlet.http.HttpServletRequest;
import javaprac.service.AccountService;
import javaprac.service.AuthenticationRefreshService;
import javaprac.service.CurrentUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/me")
public class AdminAccountController {

    private final CurrentUserService currentUserService;
    private final AccountService accountService;
    private final AuthenticationRefreshService authenticationRefreshService;

    public AdminAccountController(CurrentUserService currentUserService,
                                  AccountService accountService,
                                  AuthenticationRefreshService authenticationRefreshService) {
        this.currentUserService = currentUserService;
        this.accountService = accountService;
        this.authenticationRefreshService = authenticationRefreshService;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("user", currentUserService.currentUser());
        return "admin_me";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            RedirectAttributes ra
    ) {
        try {
            accountService.updateAdminOwnProfile(fullName, phone, address);
            ra.addFlashAttribute("successMessage", "Профиль сохранён");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/me";
    }

    @PostMapping("/credentials")
    public String updateCredentials(
            @RequestParam String email,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String newPassword2,
            HttpServletRequest request,
            RedirectAttributes ra
    ) {
        try {
            var updatedUser = accountService.updateAdminOwnCredentials(email, newPassword, newPassword2);
            authenticationRefreshService.refreshAuthentication(updatedUser.getEmail(), request);
            ra.addFlashAttribute("successMessage", "Учётные данные обновлены");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/me";
    }
}