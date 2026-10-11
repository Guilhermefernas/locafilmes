package com.locafilmes.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Lê o header "Authorization: Bearer <token>", valida o JWT e, se estiver ok,
 * autentica o usuário no SecurityContext. Se o token faltar ou for inválido,
 * apenas segue a cadeia: o AuthenticationEntryPoint responde 401 nas rotas protegidas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(PREFIXO)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(PREFIXO.length()).trim();
            if (!token.isBlank()) {
                jwtService.validarEObterEmail(token).ifPresentOrElse(email -> {
                    try {
                        UserDetails usuario = userDetailsService.loadUserByUsername(email);
                        var authentication = new UsernamePasswordAuthenticationToken(
                                usuario, null, usuario.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } catch (UsernameNotFoundException e) {
                        log.warn("Token válido para usuário inexistente: {}", email);
                    }
                }, () -> log.warn("Token JWT inválido ou expirado em {}", request.getRequestURI()));
            }
        }

        chain.doFilter(request, response);
    }
}
