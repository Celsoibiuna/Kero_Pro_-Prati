package com.keropro.service;

import com.keropro.model.ScoreProfissional;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Testes do algoritmo "Score de Excelência" — pesos fixos e média móvel de avaliações. */
class ScoreServiceTest {

    private final ScoreService scoreService = new ScoreService();

    @Test
    void calcularScoreTotalAplicaOsPesosDoModelo() {
        ScoreProfissional score = new ScoreProfissional();
        score.setFormacao(80);
        score.setCertificacoes(70);
        score.setAvaliacoes(90);
        score.setTempoResposta(60);

        int total = scoreService.calcularScoreTotal(score);

        // 80*0.30 + 70*0.25 + 90*0.30 + 60*0.15 = 24 + 17.5 + 27 + 9 = 77.5 -> arredonda para 78
        assertThat(total).isEqualTo(78);
    }

    @Test
    void aplicarNovaAvaliacaoFazMediaMovelEAtualizaOScoreTotal() {
        ScoreProfissional score = new ScoreProfissional();
        score.setFormacao(90);
        score.setCertificacoes(90);
        score.setAvaliacoes(50);
        score.setTempoResposta(90);

        scoreService.aplicarNovaAvaliacao(score, 100); // nova avaliação máxima (equivalente a 5 estrelas)

        assertThat(score.getAvaliacoes()).isEqualTo(75); // média entre 50 (antigo) e 100 (novo)
        assertThat(score.getScoreTotal()).isEqualTo(scoreService.calcularScoreTotal(score));
    }
}
