package com.keropro.controller;

import com.keropro.dto.ErroDTO;
import com.keropro.dto.LocalizacaoRequestDTO;
import com.keropro.dto.ProfissionalDTO;
import com.keropro.model.Pedido;
import com.keropro.model.Profissional;
import com.keropro.model.ScoreProfissional;
import com.keropro.model.StatusPedido;
import com.keropro.repository.PedidoRepository;
import com.keropro.repository.ProfissionalRepository;
import com.keropro.security.AuthenticatedUser;
import com.keropro.service.OrcamentoService;
import com.keropro.service.PedidoEventoService;
import com.keropro.service.ScoreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * Expõe a lista de profissionais ranqueados pelo Score de Excelência,
 * já com distância e preço estimado calculados via GPS (parâmetros lat/lng do cliente).
 */
@RestController
public class ProfissionalController {

    private static final List<StatusPedido> STATUS_ATIVOS =
            List.of(StatusPedido.ACEITO, StatusPedido.A_CAMINHO, StatusPedido.EM_EXECUCAO);

    private final ProfissionalRepository profissionalRepository;
    private final PedidoRepository pedidoRepository;
    private final ScoreService scoreService;
    private final OrcamentoService orcamentoService;
    private final PedidoEventoService pedidoEventoService;

    public ProfissionalController(ProfissionalRepository profissionalRepository,
                                   PedidoRepository pedidoRepository,
                                   ScoreService scoreService,
                                   OrcamentoService orcamentoService,
                                   PedidoEventoService pedidoEventoService) {
        this.profissionalRepository = profissionalRepository;
        this.pedidoRepository = pedidoRepository;
        this.scoreService = scoreService;
        this.orcamentoService = orcamentoService;
        this.pedidoEventoService = pedidoEventoService;
    }

    @GetMapping("/api/profissionais")
    public List<ProfissionalDTO> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(defaultValue = "-23.4950") double clienteLat,
            @RequestParam(defaultValue = "-47.4580") double clienteLng,
            @RequestParam(defaultValue = "false") boolean emergencia) {

        List<Profissional> profissionais = (categoria == null || categoria.isBlank())
                ? profissionalRepository.findByDisponivelTrue()
                : profissionalRepository.findByCategoria_SlugAndDisponivelTrue(categoria);

        return profissionais.stream()
                .map(p -> toDTO(p, clienteLat, clienteLng, emergencia))
                .sorted(Comparator.comparingInt(ProfissionalDTO::getScore).reversed())
                .toList();
    }

    /**
     * O profissional envia sua posição atual (captada via GPS nativo no app) enquanto
     * está a caminho ou em execução de um serviço. A posição é persistida e replicada
     * em tempo real para o mapa de acompanhamento de cada pedido ativo desse profissional.
     */
    @PatchMapping("/api/profissionais/localizacao")
    public ResponseEntity<?> atualizarLocalizacao(@Valid @RequestBody LocalizacaoRequestDTO body,
                                                   @AuthenticationPrincipal AuthenticatedUser usuario) {
        if (usuario == null || !"PROFISSIONAL".equals(usuario.tipo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErroDTO("Apenas profissionais podem atualizar a própria localização."));
        }

        Profissional profissional = profissionalRepository.findById(usuario.id()).orElse(null);
        if (profissional == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroDTO("Profissional não encontrado."));
        }

        profissional.setLatitude(body.getLatitude());
        profissional.setLongitude(body.getLongitude());
        profissionalRepository.save(profissional);

        List<Pedido> pedidosAtivos = pedidoRepository.findByProfissional_IdAndStatusIn(usuario.id(), STATUS_ATIVOS);
        pedidosAtivos.forEach(pedido ->
                pedidoEventoService.publicarLocalizacao(pedido.getId(), body.getLatitude(), body.getLongitude()));

        return ResponseEntity.noContent().build();
    }

    private ProfissionalDTO toDTO(Profissional p, double clienteLat, double clienteLng, boolean emergencia) {
        ScoreProfissional score = p.getScore();
        int scoreTotal = score != null ? scoreService.calcularScoreTotal(score) : 0;

        double distanciaKm = orcamentoService.calcularDistanciaKm(
                clienteLat, clienteLng, p.getLatitude().doubleValue(), p.getLongitude().doubleValue());
        BigDecimal precoEstimado = orcamentoService.calcularPrecoEstimado(p.getPrecoBase(), distanciaKm, emergencia);

        return new ProfissionalDTO(
                p.getId(), p.getUsuario().getNome(), p.getEspecialidade(), p.getCategoria().getNome(),
                scoreTotal,
                score != null ? score.getFormacao() : 0,
                score != null ? score.getCertificacoes() : 0,
                score != null ? score.getAvaliacoes() : 0,
                score != null ? score.getTempoResposta() : 0,
                p.isVerificado(), p.isDisponivel(), p.getPrecoBase(),
                Math.round(distanciaKm * 10.0) / 10.0, precoEstimado
        );
    }
}
