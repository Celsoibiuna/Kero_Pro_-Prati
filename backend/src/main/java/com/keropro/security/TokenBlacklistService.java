package com.keropro.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lista de tokens revogados por logout, mantida em memória pelo jti do JWT.
 * Cada entrada é descartada assim que o token revogado também expiraria
 * naturalmente, então a limpeza é feita de forma preguiçosa a cada consulta.
 */
@Service
public class TokenBlacklistService {

    private final Map<String, Instant> revogados = new ConcurrentHashMap<>();

    public void revogar(String jti, Instant expiraEm) {
        revogados.put(jti, expiraEm);
    }

    public boolean estaRevogado(String jti) {
        Instant expiraEm = revogados.get(jti);
        if (expiraEm == null) {
            return false;
        }
        if (expiraEm.isBefore(Instant.now())) {
            revogados.remove(jti);
            return false;
        }
        return true;
    }
}
