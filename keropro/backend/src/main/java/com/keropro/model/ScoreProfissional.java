package com.keropro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Componentes do algoritmo "Score de Excelência" (Content-Based Filtering):
 * cada dimensão é ponderada pelo ScoreService para gerar o score_total exibido no app.
 */
@Entity
@Table(name = "score_profissional")
public class ScoreProfissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id", nullable = false, unique = true)
    private Profissional profissional;

    private int formacao;
    private int certificacoes;
    private int avaliacoes;
    @Column(name = "tempo_resposta")
    private int tempoResposta;
    @Column(name = "score_total")
    private int scoreTotal;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    public Long getId() { return id; }
    public Profissional getProfissional() { return profissional; }
    public void setProfissional(Profissional profissional) { this.profissional = profissional; }
    public int getFormacao() { return formacao; }
    public void setFormacao(int formacao) { this.formacao = formacao; }
    public int getCertificacoes() { return certificacoes; }
    public void setCertificacoes(int certificacoes) { this.certificacoes = certificacoes; }
    public int getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(int avaliacoes) { this.avaliacoes = avaliacoes; }
    public int getTempoResposta() { return tempoResposta; }
    public void setTempoResposta(int tempoResposta) { this.tempoResposta = tempoResposta; }
    public int getScoreTotal() { return scoreTotal; }
    public void setScoreTotal(int scoreTotal) { this.scoreTotal = scoreTotal; }
}
