package com.keropro.security;

import java.time.Instant;

/** Identidade extraída de um JWT válido, injetada nos controllers via @AuthenticationPrincipal. */
public record AuthenticatedUser(Long id, String nome, String tipo, String jti, Instant expiraEm) {
}
