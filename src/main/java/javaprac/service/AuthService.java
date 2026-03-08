package javaprac.service;

import javaprac.dao.AppUserDao;
import javaprac.model.AppRole;
import javaprac.model.AppUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AppUserDao AppUserDao;
    private final PasswordEncoder encoder;

    public AuthService(AppUserDao AppUserDao, PasswordEncoder encoder) {
        this.AppUserDao = AppUserDao;
        this.encoder = encoder;
    }

    @Transactional
    public AppUser register(String email, String fullName, String password, String password2) {
        String e = email == null ? null : email.trim().toLowerCase();
        String normalizedName = fullName == null ? "" : fullName.trim();

        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("Требуется полное имя");
        }
        if (e == null || e.isBlank()) {
            throw new IllegalArgumentException("Требуется Email");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Слишком короткий пароль");
        }
        if (!password.equals(password2)) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
        if (AppUserDao.findByEmail(e).isPresent()) {
            throw new IllegalArgumentException("Email уже зарегистрирован");
        }

        AppUser u = new AppUser();
        u.setEmail(e);
        u.setPasswordHash(encoder.encode(password));
        u.setRole(AppRole.USER);
        u.setEnabled(true);
        u.setFullName(normalizedName);

        return AppUserDao.save(u);
    }
}