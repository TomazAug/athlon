package br.com.athlon.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Se usuário já estiver logado
            String currentUri = org.springframework.web.context.request.RequestContextHolder.getRequestAttributes() instanceof org.springframework.web.context.request.ServletRequestAttributes
                    ? ((org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).getRequest().getRequestURI()
                    : "";
            if ("/atleta/form-inserir".equals(currentUri)) {
                return "/atleta/form-inserir";
            } else if ("/atleta/salvar".equals(currentUri)) {
                return "/atleta/salvar";
            }
            if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
                boolean isAdmin = auth.getAuthorities().stream()
                        .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));
                if (isAdmin) {
                    return "redirect:/atleta";
                }
                return "redirect:/atividade";
            }
            return "login";
        }
}
