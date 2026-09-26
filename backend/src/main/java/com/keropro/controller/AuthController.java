package com.keropro.controller;

import com.keropro.dto.ErroDTO;
import com.keropro.dto.LoginRequestDTO;
import com.keropro.dto.LoginResponseDTO;
import com.keropro.dto.SessaoDTO;
import com.keropro.model.Usuario;
import com.keropro.repository.UsuarioRepository;
import com.keropro.security.AuthenticatedUser;
import com.keropro.security.JwtService;
import com.keropro.security.TokenBlacklistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Autenticação via JWT assinado (HS256). A senha é conferida com BCrypt contra
 * o hash armazenado — nunca em texto puro — e o token emitido carrega id/nome/tipo
 * do usuário, permitindo que os demais endpoints validem posse dos recursos.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                           JwtService jwtService, TokenBlacklistService tokenBlacklistService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO body) {
        Usuario usuario = usuarioRepository.findByEmail(body.getEmail().toLowerCase().trim()).orElse(null);
        if (usuario == null || !passwordEncoder.matches(body.getSenha(), usuario.getSenhaHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroDTO("E-mail ou senha inválidos."));
        }

        usuario.setUltimoLoginEm(LocalDateTime.now());
        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario);
        return ResponseEntity.ok(new LoginResponseDTO(usuario.getId(), usuario.getNome(), usuario.getTipo().name(), token));
    }

    /** Usado pela tela splash para checar se a sessão salva no dispositivo ainda é válida. */
    @GetMapping("/me")
    public ResponseEntity<SessaoDTO> sessaoAtual(@AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(new SessaoDTO(usuario.id(), usuario.nome(), usuario.tipo()));
    }

    /** Encerra a sessão revogando o token atual — ele deixa de ser aceito mesmo antes de expirar. */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthenticatedUser usuario) {
        tokenBlacklistService.revogar(usuario.jti(), usuario.expiraEm());
        return ResponseEntity.noContent().build();
    }
}
