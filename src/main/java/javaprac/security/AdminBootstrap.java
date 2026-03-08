package javaprac.security;

import javaprac.dao.AppUserDao;
import javaprac.model.AppRole;
import javaprac.model.AppUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final AppUserDao AppUserDao;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin}")
    private String adminPassword;

    public AdminBootstrap(AppUserDao AppUserDao, PasswordEncoder passwordEncoder) {
        this.AppUserDao = AppUserDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        String email = adminEmail == null ? "" : adminEmail.trim().toLowerCase();
        if (email.isEmpty()) return;

        AppUserDao.findByEmail(email).orElseGet(() -> {
            AppUser u = new AppUser();
            u.setEmail(email);
            u.setRole(AppRole.ADMIN);
            u.setEnabled(true);
            u.setFullName("Administrator");
            u.setPasswordHash(passwordEncoder.encode(adminPassword));
            return AppUserDao.save(u);
        });
    }
}