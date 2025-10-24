package br.com.athlon.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Formulário e salvar cadastro liberados
                        .requestMatchers("/atleta/form-inserir/**").permitAll()

                        // Rotas restritas a ADMIN
                        .requestMatchers("/admin/**", ("/atleta/**")).hasRole("ADMIN")

                        // Rotas de atividades para USER ou ADMIN
                        .requestMatchers("/atividade/**").hasAnyRole("USER", "ADMIN")

                        // Recursos públicos
                        .requestMatchers("/", "/index", "/home", "/css/**", "/js/**", "/images/**", "/login", "/assets/**", "/fragments/**", "/atleta/salvar","/atleta/form-inserir").permitAll()

                        // Qualquer outra requisição precisa estar autenticada
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));
                            boolean isUser = authentication.getAuthorities().stream()
                                    .anyMatch(ga -> ga.getAuthority().equals("ROLE_USER"));

                            if (isAdmin) {
                                response.sendRedirect("/atleta");
                            } else if (isUser) {
                                response.sendRedirect("/atividade");
                            } else {
                                response.sendRedirect("/"); // fallback
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
