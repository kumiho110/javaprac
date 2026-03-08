package javaprac.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javaprac.dao.AppUserDao;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class DisabledUserLogoutFilter extends OncePerRequestFilter {

    private final AppUserDao appUserDao;

    public DisabledUserLogoutFilter(AppUserDao appUserDao) {
        this.appUserDao = appUserDao;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String email = auth.getName();

            boolean enabled = appUserDao.findByEmail(email)
                    .map(u -> u.isEnabled())
                    .orElse(false);

            if (!enabled) {
                SecurityContextHolder.clearContext();

                var session = request.getSession(false);
                if (session != null) session.invalidate();

                response.sendRedirect("/login?disabled=1");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}