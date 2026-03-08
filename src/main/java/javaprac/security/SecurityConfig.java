package javaprac.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler handler =
                new SavedRequestAwareAuthenticationSuccessHandler() {
                    @Override
                    protected String determineTargetUrl(
                            jakarta.servlet.http.HttpServletRequest request,
                            jakarta.servlet.http.HttpServletResponse response,
                            Authentication authentication
                    ) {
                        String continueUrl = request.getParameter("continue");
                        if (isSafeLocalTarget(continueUrl)) {
                            return continueUrl;
                        }

                        boolean isAdmin = authentication.getAuthorities().stream()
                                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

                        boolean isManager = authentication.getAuthorities().stream()
                                .anyMatch(a -> "ROLE_MANAGER".equals(a.getAuthority()));

                        return isAdmin ? "/admin" : isManager? "/staff/orders" : "/products";
                    }
                };

        handler.setTargetUrlParameter("continue");
        return handler;
    }

    private boolean isSafeLocalTarget(String target) {
        return target != null
                && !target.isBlank()
                && target.startsWith("/")
                && !target.startsWith("//")
                && !target.startsWith("/\\");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationSuccessHandler authenticationSuccessHandler,
                                           DisabledUserLogoutFilter disabledUserLogoutFilter) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/register", "/error",
                                "/products", "/products/*",
                                "/products/attribute-names", "/products/attribute-values"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/staff/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/cart/**", "/my/**").hasRole("USER")
                        .requestMatchers("/me").hasAnyRole("USER", "MANAGER")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            String continueUrl = request.getParameter("continue");

                            String target = "/login?error";
                            if (isSafeLocalTarget(continueUrl)) {
                                String encoded = java.net.URLEncoder.encode(
                                        continueUrl,
                                        java.nio.charset.StandardCharsets.UTF_8
                                );
                                target += "&continue=" + encoded;
                            }

                            response.sendRedirect(target);
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                )
                .addFilterAfter(disabledUserLogoutFilter, SecurityContextHolderFilter.class)
                .build();
    }
}