package javaprac.service;

import javaprac.model.AppUser;
import javaprac.dao.AppUserDao;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final AppUserDao appUserDao;

    public CurrentUserService(AppUserDao appUserDao) {
        this.appUserDao = appUserDao;
    }

    public AppUser currentUser() {
        AppUser user = currentUserOrNull();
        if (user == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }
        return user;
    }

    public AppUser currentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        String email = auth.getName() == null ? null : auth.getName().trim().toLowerCase();
        if (email == null || email.isBlank()) {
            return null;
        }

        return appUserDao.findByEmail(email).orElse(null);
    }
}