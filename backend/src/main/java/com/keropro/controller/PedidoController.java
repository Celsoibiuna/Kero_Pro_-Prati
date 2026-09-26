package com.keropro.controller;

import com.keropro.dto.*;
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
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/** Endpoints de criação e acompanhamento de pedidos (fluxo cliente <-> profissional). */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final OrcamentoService orcamentoService;
    private final PedidoService pedidoService;
    private final ScoreService scoreService;
    private final PedidoEventoService pedidoEventoService;
    private final EntityManager entityManager;

    public PedidoController(PedidoRepository pedidoRepository,
                             ProfissionalRepository profissionalRepository,
                             AvaliacaoRepository avaliacaoRepository,
                             OrcamentoService orcamentoService,
                             PedidoService pedidoService,
                             ScoreService scoreService,
                             PedidoEventoService pedidoEventoService,
                             EntityManager entityManager) {
        this.pedidoRepository = pedidoRepository;
        this.profissionalRepository = profissionalRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.orcamentoService = orcamentoService;
        this.pedidoService = pedidoService;
        this.scoreService = scoreService;
        this.pedidoEventoService = pedidoEventoService;
        this.entityManager = entityManager;
    }

    /** Painel do profissional: pedidos pendentes na região. Só o próprio profissional pode consultar. */
    @GetMapping("/pendentes/{profissionalId}")
    public ResponseEntity<?> pendentes(@PathVariable Long profissionalId,
                                        @AuthenticationPrincipal AuthenticatedUser usuario) {
        if (!ehDonoProfissional(usuario, profissionalId)) {
            return erro(HttpStatus.FORBIDDEN, "Você só pode ver os pedidos pendentes da sua própria conta profissional.");
        }
        return ResponseEntity.ok(pedidoRepository.findByProfissional_IdAndStatus(profissionalId, StatusPedido.PENDENTE));
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody PedidoRequestDTO body,
                                    @AuthenticationPrincipal AuthenticatedUser usuario) {
        if (!ehDonoCliente(usuario, body.getClienteId())) {
            return erro(HttpStatus.FORBIDDEN, "Você só pode criar pedidos para a sua própria conta de cliente.");
        }
        Profissional profissional = profissionalRepository.findById(body.getProfissionalId()).orElse(null);
        if (profissional == null) {
            return erro(HttpStatus.NOT_FOUND, "Profissional não encontrado.");
        }

        double distanciaKm = orcamentoService.calcularDistanciaKm(
                body.getClienteLatitude(), body.getClienteLongitude(),
                profissional.getLatitude().doubleValue(), profissional.getLongitude().doubleValue());

        Pedido pedido = new Pedido();
        pedido.setCliente(entityManager.getReference(Cliente.class, body.getClienteId()));
        pedido.setProfissional(profissional);
        pedido.setCategoria(profissional.getCategoria());
        pedido.setDescricao(body.getDescricao());
        pedido.setDistanciaKm(BigDecimal.valueOf(distanciaKm));
        pedido.setValorEstimado(orcamentoService.calcularPrecoEstimado(
                profissional.getPrecoBase(), distanciaKm, body.isEmergencia()));
        pedido.setEmergencia(body.isEmergencia());
        pedido.setStatus(StatusPedido.PENDENTE);

        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.ok(toDTO(salvo));
    }

    /** Profissional aceita um pedido pendente endereçado a ele. */
    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<?> aceitar(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser usuario) {
        return transicionarComPosse(id, usuario, StatusPedido.PENDENTE, StatusPedido.ACEITO);
    }

    /** Profissional recusa um pedido pendente endereçado a ele. */
    @PatchMapping("/{id}/recusar")
    public ResponseEntity<?> recusar(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser usuario) {
        return transicionarComPosse(id, usuario, StatusPedido.PENDENTE, StatusPedido.RECUSADO);
    }

    /** Avança o status do pedido em uma etapa (Aceito -> A caminho -> Em execução -> Concluído). */
    @PatchMapping("/{id}/avancar")
    public ResponseEntity<?> avancarStatus(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser usuario) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            return erro(HttpStatus.NOT_FOUND, "Pedido não encontrado.");
        }
        if (!ehDonoProfissional(usuario, pedido.getProfissional().getId())) {
            return erro(HttpStatus.FORBIDDEN, "Você não tem permissão para alterar este pedido.");
        }
        if (pedidoService.podeAvancar(pedido.getStatus())) {
            pedido.setStatus(pedidoService.avancar(pedido.getStatus()));
            pedidoRepository.save(pedido);
            pedidoEventoService.publicarStatus(pedido);
        }
        return ResponseEntity.ok(toDTO(pedido));
    }

    /** Cliente avalia um pedido concluído — a nota alimenta o Score de Excelência do profissional. */
    @PostMapping("/{id}/avaliar")
    @Transactional
    public ResponseEntity<?> avaliar(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequestDTO body,
                                      @AuthenticationPrincipal AuthenticatedUser usuario) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            return erro(HttpStatus.NOT_FOUND, "Pedido não encontrado.");
        }
        if (!ehDonoCliente(usuario, pedido.getCliente().getId())) {
            return erro(HttpStatus.FORBIDDEN, "Você só pode avaliar os seus próprios pedidos.");
        }
        if (pedido.getStatus() != StatusPedido.CONCLUIDO) {
            return erro(HttpStatus.CONFLICT, "Só é possível avaliar pedidos concluídos.");
        }
        if (avaliacaoRepository.findByPedido_Id(id).isPresent()) {
            return erro(HttpStatus.CONFLICT, "Este pedido já foi avaliado.");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setPedido(pedido);
        avaliacao.setNota(body.getNota());
        avaliacao.setComentario(body.getComentario());
        avaliacaoRepository.save(avaliacao);

        ScoreProfissional score = pedido.getProfissional().getScore();
        if (score != null) {
            scoreService.aplicarNovaAvaliacao(score, body.getNota() * 20);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new AvaliacaoResponseDTO(
                avaliacao.getId(), avaliacao.getNota(), avaliacao.getComentario(),
                score != null ? score.getScoreTotal() : 0));
    }

    private ResponseEntity<?> transicionarComPosse(Long pedidoId, AuthenticatedUser usuario,
                                                     StatusPedido statusEsperado, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        if (pedido == null) {
            return erro(HttpStatus.NOT_FOUND, "Pedido não encontrado.");
        }
        if (!ehDonoProfissional(usuario, pedido.getProfissional().getId())) {
            return erro(HttpStatus.FORBIDDEN, "Você não tem permissão para alterar este pedido.");
        }
        if (pedido.getStatus() != statusEsperado) {
            return erro(HttpStatus.CONFLICT, "Este pedido não está mais pendente.");
        }
        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);
        pedidoEventoService.publicarStatus(pedido);
        return ResponseEntity.ok(toDTO(pedido));
    }

    private boolean ehDonoProfissional(AuthenticatedUser usuario, Long profissionalId) {
        return usuario != null && "PROFISSIONAL".equals(usuario.tipo()) && usuario.id().equals(profissionalId);
    }

    private boolean ehDonoCliente(AuthenticatedUser usuario, Long clienteId) {
        return usuario != null && "CLIENTE".equals(usuario.tipo()) && usuario.id().equals(clienteId);
    }

    private ResponseEntity<?> erro(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(new ErroDTO(mensagem));
    }

    private PedidoResponseDTO toDTO(Pedido p) {
        return new PedidoResponseDTO(
                p.getId(),
                p.getCliente().getUsuario() != null ? p.getCliente().getUsuario().getNome() : null,
                p.getProfissional().getUsuario().getNome(),
                p.getDescricao(), p.getStatus().name(),
                p.getDistanciaKm(), p.getValorEstimado(), p.isEmergencia());
    }
}
