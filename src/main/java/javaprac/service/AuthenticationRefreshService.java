package javaprac.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationRefreshService {

    private final UserDetailsService userDetailsService;

    public AuthenticationRefreshService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void refreshAuthentication(String username, HttpServletRequest request) {
        var ud = userDetailsService.loadUserByUsername(username);
        var auth = new UsernamePasswordAuthenticationToken(
                ud, ud.getPassword(), ud.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession(true).setAttribute(
                "SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext()
        );
    }

    public void loginUser(String username, HttpServletRequest request) {
        refreshAuthentication(username, request);
    }
}