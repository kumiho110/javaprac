package javaprac.service;

import javaprac.model.AppUser;
import javaprac.dao.AppUserDao;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AppUserDao appUserDao;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AppUserDao appUserDao,
                          CurrentUserService currentUserService,
                          PasswordEncoder passwordEncoder) {
        this.appUserDao = appUserDao;
        this.currentUserService = currentUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void updateOwnProfile(String fullName, String phone, String address) {
        AppUser user = currentUserService.currentUser();

        String normalizedName = fullName == null ? "" : fullName.trim();
        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("Имя обязательно");
        }

        user.setFullName(normalizedName);
        user.setPhone(phone == null || phone.isBlank() ? null : phone.trim());
        user.setAddress(address == null || address.isBlank() ? null : address.trim());

    }

    @Transactional
    public void changeOwnPassword(String currentPassword, String newPassword, String newPassword2) {
        AppUser user = currentUserService.currentUser();

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("Введите текущий пароль");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Текущий пароль неверный");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Новый пароль должен быть не короче 6 символов");
        }
        if (!newPassword.equals(newPassword2)) {
            throw new IllegalArgumentException("Подтверждение нового пароля не совпадает");
        }
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Новый пароль должен отличаться от текущего");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void updateAdminOwnProfile(String fullName, String phone, String address) {
        AppUser user = currentUserService.currentUser();

        String normalizedName = fullName == null ? "" : fullName.trim();
        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("ФИО обязательно");
        }

        user.setFullName(normalizedName);
        user.setPhone(phone == null || phone.isBlank() ? null : phone.trim());
        user.setAddress(address == null || address.isBlank() ? null : address.trim());

    }

    @Transactional
    public AppUser updateAdminOwnCredentials(String email, String newPassword, String newPassword2) {
        AppUser user = currentUserService.currentUser();

        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        if (normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email обязателен");
        }

        var existing = appUserDao.findByEmail(normalizedEmail);
        if (existing.isPresent() && !existing.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Такой email уже занят");
        }

        String p1 = newPassword == null ? "" : newPassword;
        String p2 = newPassword2 == null ? "" : newPassword2;

        boolean wantsPasswordChange = !p1.isBlank() || !p2.isBlank();

        user.setEmail(normalizedEmail);

        if (wantsPasswordChange) {
            if (p1.length() < 6) {
                throw new IllegalArgumentException("Новый пароль должен быть не короче 6 символов");
            }
            if (!p1.equals(p2)) {
                throw new IllegalArgumentException("Подтверждение нового пароля не совпадает");
            }

            user.setPasswordHash(passwordEncoder.encode(p1));
        }

        return user;
    }
}