package com.keropro.controller;

import com.keropro.dto.LoginRequestDTO;
import com.keropro.dto.LoginResponseDTO;
import com.keropro.model.Usuario;
import com.keropro.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Autenticação simplificada para fins de demonstração acadêmica.
 * Em produção, substituir a verificação de senha por BCrypt e o token por JWT assinado.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO body) {
        return usuarioRepository.findByEmail(body.getEmail())
                .map(u -> ResponseEntity.ok(new LoginResponseDTO(
                        u.getId(), u.getNome(), u.getTipo().name(), UUID.randomUUID().toString())))
                .orElse(ResponseEntity.status(401).build());
    }
}
