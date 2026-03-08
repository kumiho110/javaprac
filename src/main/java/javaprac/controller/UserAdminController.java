package javaprac.controller;

import javaprac.model.AppRole;
import javaprac.service.UserAdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public String usersPage(Model model) {
        model.addAttribute("users", userAdminService.listManageableUsers());
        return "users_admin";
    }

    @GetMapping("/new")
    public String newUserPage(Model model) {
        model.addAttribute("roles", java.util.List.of(AppRole.USER, AppRole.MANAGER));
        return "user_create_admin";
    }

    @PostMapping
    public String createUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String password2,
            @RequestParam String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam AppRole role,
            @RequestParam(defaultValue = "true") boolean enabled,
            RedirectAttributes ra
    ) {
        try {
            userAdminService.createUserByAdmin(
                    email,
                    password,
                    password2,
                    fullName,
                    phone,
                    address,
                    role,
                    enabled
            );

            ra.addFlashAttribute("successMessage", "Пользователь создан");
            return "redirect:/admin/users";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/users/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editUserPage(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("user", userAdminService.getManageableUserForEdit(id));
            return "user_form_admin";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/{id}")
    public String updateUser(
            @PathVariable Long id,
            @RequestParam String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam(defaultValue = "false") boolean enabled,
            RedirectAttributes ra
    ) {
        try {
            userAdminService.updateUserProfileByAdmin(id, fullName, phone, address, enabled);
            ra.addFlashAttribute("successMessage", "Пользователь сохранён");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/toggle-enabled")
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes ra) {
        try {
            var user = userAdminService.toggleUserEnabled(id);
            ra.addFlashAttribute("successMessage",
                    user.isEnabled() ? "Пользователь включён" : "Пользователь отключён");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/credentials")
    public String updateCredentials(
            @PathVariable Long id,
            @RequestParam String email,
            @RequestParam(required = false) String resetValue1,
            @RequestParam(required = false) String resetValue2,
            RedirectAttributes ra
    ) {
        try {
            userAdminService.updateUserCredentialsByAdmin(id, email, resetValue1, resetValue2);
            ra.addFlashAttribute("successMessage", "Учётные данные обновлены");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/users/" + id + "/edit";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userAdminService.deleteUser(id);
            ra.addFlashAttribute("successMessage", "Пользователь удалён");
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/users";
    }
}