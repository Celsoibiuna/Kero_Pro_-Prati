package com.keropro.service;

import com.keropro.model.ScoreProfissional;
import org.springframework.stereotype.Service;

/**
 * Implementa o algoritmo "Score de Excelência" descrito na proposta do KeroPro,
 * inspirado em Content-Based Filtering (Lops et al., 2011): cada profissional é
 * representado por um vetor de atributos de competência, combinado por pesos fixos
 * definidos pela curadoria acadêmica do projeto.
 */
@Service
public class ScoreService {

    // Pesos do modelo (somam 1.0) — calibrados para valorizar formação e certificação,
    // conforme o diferencial de proposta de valor do KeroPro.
    private static final double PESO_FORMACAO       = 0.30;
    private static final double PESO_CERTIFICACOES  = 0.25;
    private static final double PESO_AVALIACOES     = 0.30;
    private static final double PESO_TEMPO_RESPOSTA = 0.15;

    /** Recalcula e retorna o score_total (0-100) a partir dos quatro componentes. */
    public int calcularScoreTotal(ScoreProfissional score) {
        double total =
                score.getFormacao()       * PESO_FORMACAO +
                score.getCertificacoes()  * PESO_CERTIFICACOES +
                score.getAvaliacoes()     * PESO_AVALIACOES +
                score.getTempoResposta()  * PESO_TEMPO_RESPOSTA;
        return (int) Math.round(total);
    }

    /**
     * Atualiza o score de um profissional após uma nova avaliação de cliente,
     * usando média móvel simples entre o valor atual e a nova nota (0-100).
     */
    public void aplicarNovaAvaliacao(ScoreProfissional score, int notaConvertidaEm100) {
        int novaMediaAvaliacoes = (score.getAvaliacoes() + notaConvertidaEm100) / 2;
        score.setAvaliacoes(novaMediaAvaliacoes);
        score.setScoreTotal(calcularScoreTotal(score));
    }
}
