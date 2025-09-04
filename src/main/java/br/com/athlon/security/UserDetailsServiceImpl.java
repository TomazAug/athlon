package br.com.athlon.security;

import br.com.athlon.model.User;
import br.com.athlon.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Primary
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Remove o prefixo "ROLE_" pois o método .roles() adiciona ele automaticamente
        String roleSemPrefixo = user.getRole().replace("ROLE_", "");

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(roleSemPrefixo) // Passa só "ADMIN" ou "USER"
                .build();
    }
}
