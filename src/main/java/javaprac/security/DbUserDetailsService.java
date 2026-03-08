package javaprac.security;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import javaprac.dao.AppUserDao;

@Service
public class DbUserDetailsService implements UserDetailsService {
    private final AppUserDao dao;

    public DbUserDetailsService(AppUserDao dao) {
        this.dao = dao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();

        var u = dao.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + normalizedEmail));

        var auth = List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()));
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPasswordHash(),
                u.isEnabled(),
                true, true, true,
                auth
        );
    }
}