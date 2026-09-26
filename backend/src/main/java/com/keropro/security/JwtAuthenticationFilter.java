package com.keropro.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Lê o header Authorization em cada requisição e, se houver um JWT válido e não
 * revogado, autentica a requisição no SecurityContext — sem sessão de servidor,
 * toda a informação necessária vive no próprio token.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;

    public JwtAuthenticationFilter(JwtService jwtService, TokenBlacklistService blacklistService) {
        this.jwtService = jwtService;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.validarEExtrairClaims(header.substring(7));
                if (!blacklistService.estaRevogado(claims.getId())) {
                    AuthenticatedUser usuario = new AuthenticatedUser(
                            Long.valueOf(claims.getSubject()),
                            claims.get("nome", String.class),
                            claims.get("tipo", String.class),
                            claims.getId(),
                            claims.getExpiration().toInstant());
                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.tipo()));
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(usuario, null, authorities));
                }
            } catch (JwtException | IllegalArgumentException tokenInvalido) {
                // Token ausente/expirado/malformado: segue sem autenticar.
                // Endpoints protegidos barram a requisição via 401 na cadeia do Spring Security.
            }
        }
        filterChain.doFilter(request, response);
    }
}
