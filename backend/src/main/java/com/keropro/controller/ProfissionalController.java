package com.keropro.controller;

import com.keropro.dto.ProfissionalDTO;
import com.keropro.model.Profissional;
import com.keropro.model.ScoreProfissional;
import com.keropro.repository.ProfissionalRepository;
import com.keropro.service.OrcamentoService;
import com.keropro.service.ScoreService;
import org.springframework.web.bind.annotation.GetMapping;
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

    private final ProfissionalRepository profissionalRepository;
    private final ScoreService scoreService;
    private final OrcamentoService orcamentoService;

    public ProfissionalController(ProfissionalRepository profissionalRepository,
                                   ScoreService scoreService,
                                   OrcamentoService orcamentoService) {
        this.profissionalRepository = profissionalRepository;
        this.scoreService = scoreService;
        this.orcamentoService = orcamentoService;
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
