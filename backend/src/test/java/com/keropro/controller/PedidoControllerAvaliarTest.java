package com.keropro.controller;

import com.keropro.dto.AvaliacaoRequestDTO;
import com.keropro.dto.AvaliacaoResponseDTO;
import com.keropro.model.*;
import com.keropro.repository.AvaliacaoRepository;
import com.keropro.repository.PedidoRepository;
import com.keropro.repository.ProfissionalRepository;
import com.keropro.security.AuthenticatedUser;
import com.keropro.service.OrcamentoService;
import com.keropro.service.PedidoEventoService;
import com.keropro.service.PedidoService;
import com.keropro.service.ScoreService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * POST /api/pedidos/{id}/avaliar é o endpoint que fecha o ciclo do Score de
 * Excelência: só deve funcionar para o cliente dono do pedido, só com o pedido
 * concluído, nunca duas vezes, e precisa repassar a nota já convertida (1-5
 * estrelas -> escala 0-100) para o ScoreService.
 */
@ExtendWith(MockitoExtension.class)
class PedidoControllerAvaliarTest {

    private static final Long PEDIDO_ID = 10L;
    private static final Long CLIENTE_ID = 1L;
    private static final Long PROFISSIONAL_ID = 2L;

    @Mock private PedidoRepository pedidoRepository;
    @Mock private ProfissionalRepository profissionalRepository;
    @Mock private AvaliacaoRepository avaliacaoRepository;
    @Mock private OrcamentoService orcamentoService;
    @Mock private PedidoService pedidoService;
    @Mock private ScoreService scoreService;
    @Mock private PedidoEventoService pedidoEventoService;
    @Mock private EntityManager entityManager;

    private PedidoController pedidoController;

    @BeforeEach
    void setUp() {
        pedidoController = new PedidoController(pedidoRepository, profissionalRepository, avaliacaoRepository,
                orcamentoService, pedidoService, scoreService, pedidoEventoService, entityManager);
    }

    private AuthenticatedUser clienteDono() {
        return new AuthenticatedUser(CLIENTE_ID, "Mariana Oliveira", "CLIENTE", "jti-1", Instant.now().plusSeconds(3600));
    }

    private Pedido pedidoConcluido() {
        Usuario usuarioCliente = new Usuario();
        usuarioCliente.setId(CLIENTE_ID);
        Cliente cliente = new Cliente();
        cliente.setUsuario(usuarioCliente);

        Usuario usuarioProfissional = new Usuario();
        usuarioProfissional.setId(PROFISSIONAL_ID);
        Profissional profissional = new Profissional();
        profissional.setUsuario(usuarioProfissional);
        profissional.setScore(new ScoreProfissional());

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProfissional(profissional);
        pedido.setStatus(StatusPedido.CONCLUIDO);
        return pedido;
    }

    private AvaliacaoRequestDTO corpoAvaliacao(int nota) {
        AvaliacaoRequestDTO dto = new AvaliacaoRequestDTO();
        dto.setNota(nota);
        dto.setComentario("Ótimo atendimento");
        return dto;
    }

    @Test
    void avaliarPedidoDeOutroClienteRetorna403ENaoTocaNoScore() {
        when(pedidoRepository.findById(PEDIDO_ID)).thenReturn(Optional.of(pedidoConcluido()));
        AuthenticatedUser outroCliente = new AuthenticatedUser(999L, "Outro", "CLIENTE", "jti-2", Instant.now().plusSeconds(3600));

        ResponseEntity<?> resposta = pedidoController.avaliar(PEDIDO_ID, corpoAvaliacao(5), outroCliente);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verifyNoInteractions(scoreService, avaliacaoRepository);
    }

    @Test
    void avaliarPedidoAindaNaoConcluidoRetorna409() {
        Pedido pedido = pedidoConcluido();
        pedido.setStatus(StatusPedido.EM_EXECUCAO);
        when(pedidoRepository.findById(PEDIDO_ID)).thenReturn(Optional.of(pedido));

        ResponseEntity<?> resposta = pedidoController.avaliar(PEDIDO_ID, corpoAvaliacao(5), clienteDono());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verifyNoInteractions(scoreService, avaliacaoRepository);
    }

    @Test
    void avaliarPedidoJaAvaliadoRetorna409ENaoDuplicaAvaliacao() {
        when(pedidoRepository.findById(PEDIDO_ID)).thenReturn(Optional.of(pedidoConcluido()));
        when(avaliacaoRepository.findByPedido_Id(PEDIDO_ID)).thenReturn(Optional.of(new Avaliacao()));

        ResponseEntity<?> resposta = pedidoController.avaliar(PEDIDO_ID, corpoAvaliacao(5), clienteDono());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(scoreService, never()).aplicarNovaAvaliacao(any(), anyInt());
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void avaliarComSucessoConverteNotaDeEstrelasParaEscala0a100EChamaOScoreService() {
        Pedido pedido = pedidoConcluido();
        when(pedidoRepository.findById(PEDIDO_ID)).thenReturn(Optional.of(pedido));
        when(avaliacaoRepository.findByPedido_Id(PEDIDO_ID)).thenReturn(Optional.empty());

        // 4 estrelas -> 80 na escala 0-100 usada pelo Score de Excelência
        ResponseEntity<?> resposta = pedidoController.avaliar(PEDIDO_ID, corpoAvaliacao(4), clienteDono());

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(scoreService).aplicarNovaAvaliacao(pedido.getProfissional().getScore(), 80);
        verify(avaliacaoRepository).save(any(Avaliacao.class));

        AvaliacaoResponseDTO corpo = (AvaliacaoResponseDTO) resposta.getBody();
        assertThat(corpo).isNotNull();
        assertThat(corpo.getNota()).isEqualTo(4);
    }
}
