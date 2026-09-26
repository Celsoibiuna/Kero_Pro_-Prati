package com.keropro.controller;

import com.keropro.dto.LoginRequestDTO;
import com.keropro.dto.LoginResponseDTO;
import com.keropro.model.TipoUsuario;
import com.keropro.model.Usuario;
import com.keropro.repository.UsuarioRepository;
import com.keropro.security.JwtService;
import com.keropro.security.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Login deve conferir a senha com BCrypt (nunca em texto puro) e só então
 * emitir um JWT real — este teste cobre exatamente essa correção.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private TokenBlacklistService tokenBlacklistService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(usuarioRepository, passwordEncoder, jwtService, tokenBlacklistService);
    }

    private Usuario usuarioDemo() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Mariana Oliveira");
        usuario.setEmail("mariana@keropro.com");
        usuario.setSenhaHash("hash-bcrypt-armazenado");
        usuario.setTipo(TipoUsuario.CLIENTE);
        return usuario;
    }

    @Test
    void loginComSenhaErradaRetorna401ENaoGeraToken() {
        Usuario usuario = usuarioDemo();
        when(usuarioRepository.findByEmail("mariana@keropro.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha-errada", usuario.getSenhaHash())).thenReturn(false);

        LoginRequestDTO body = new LoginRequestDTO();
        body.setEmail("mariana@keropro.com");
        body.setSenha("senha-errada");

        ResponseEntity<?> resposta = authController.login(body);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtService, never()).gerarToken(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void loginComEmailInexistenteRetorna401SemDistinguirDoErroDeSenha() {
        when(usuarioRepository.findByEmail("naoexiste@keropro.com")).thenReturn(Optional.empty());

        LoginRequestDTO body = new LoginRequestDTO();
        body.setEmail("naoexiste@keropro.com");
        body.setSenha("qualquer-coisa");

        ResponseEntity<?> resposta = authController.login(body);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtService, never()).gerarToken(any());
    }

    @Test
    void loginComSenhaCorretaGeraTokenJwt() {
        Usuario usuario = usuarioDemo();
        when(usuarioRepository.findByEmail("mariana@keropro.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Senha@123", usuario.getSenhaHash())).thenReturn(true);
        when(jwtService.gerarToken(usuario)).thenReturn("token-jwt-assinado");

        LoginRequestDTO body = new LoginRequestDTO();
        body.setEmail("mariana@keropro.com");
        body.setSenha("Senha@123");

        ResponseEntity<?> resposta = authController.login(body);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponseDTO corpo = (LoginResponseDTO) resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.getToken()).isEqualTo("token-jwt-assinado");
        assertThat(corpo.getTipo()).isEqualTo("CLIENTE");
        verify(usuarioRepository).save(usuario);
    }
}
