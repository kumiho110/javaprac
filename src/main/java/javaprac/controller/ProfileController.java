package javaprac.controller;

import javaprac.service.AccountService;
import javaprac.service.CurrentUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final CurrentUserService currentUserService;
    private final AccountService accountService;

    public ProfileController(CurrentUserService currentUserService, AccountService accountService) {
        this.currentUserService = currentUserService;
        this.accountService = accountService;
    }

    @GetMapping("/me")
    public String profilePage(Model model) {
        model.addAttribute("user", currentUserService.currentUser());
        return "profile";
    }

    @PostMapping("/me")
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            RedirectAttributes ra
    ) {
        try {
            accountService.updateOwnProfile(fullName, phone, address);
            ra.addFlashAttribute("success", "Профиль сохранён");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/me";
    }

    @PostMapping("/me/password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String newPassword2,
            RedirectAttributes ra
    ) {
        try {
            accountService.changeOwnPassword(currentPassword, newPassword, newPassword2);
            ra.addFlashAttribute("success", "Пароль успешно изменён");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/me";
    }
}