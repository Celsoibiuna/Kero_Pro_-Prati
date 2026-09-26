package com.keropro.security;

import com.keropro.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Emite e valida os JWTs de sessão (HS256). Cada token carrega um jti único,
 * usado pelo TokenBlacklistService para permitir logout antes da expiração natural.
 */
@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMinutos;

    public JwtService(@Value("${keropro.jwt.secret}") String secret,
                       @Value("${keropro.jwt.expiracao-minutos}") long expiracaoMinutos) {
        this.chave = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMinutos = expiracaoMinutos;
    }

    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(expiracaoMinutos * 60);
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(usuario.getId()))
                .claim("nome", usuario.getNome())
                .claim("tipo", usuario.getTipo().name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .signWith(chave)
                .compact();
    }

    /** @throws io.jsonwebtoken.JwtException se o token for inválido, malformado ou estiver expirado. */
    public Claims validarEExtrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
