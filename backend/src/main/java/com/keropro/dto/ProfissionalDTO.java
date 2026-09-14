package com.keropro.dto;

import java.math.BigDecimal;

/** Representação de profissional enviada ao front-end, já com score calculado e preço estimado. */
public class ProfissionalDTO {
    private Long id;
    private String nome;
    private String especialidade;
    private String categoria;
    private int score;
    private int formacao;
    private int certificacoes;
    private int avaliacoesScore;
    private int tempoResposta;
    private boolean verificado;
    private boolean disponivel;
    private BigDecimal precoBase;
    private double distanciaKm;
    private BigDecimal precoEstimado;

    public ProfissionalDTO(Long id, String nome, String especialidade, String categoria,
                            int score, int formacao, int certificacoes, int avaliacoesScore, int tempoResposta,
                            boolean verificado, boolean disponivel, BigDecimal precoBase,
                            double distanciaKm, BigDecimal precoEstimado) {
        this.id = id; this.nome = nome; this.especialidade = especialidade; this.categoria = categoria;
        this.score = score; this.formacao = formacao; this.certificacoes = certificacoes;
        this.avaliacoesScore = avaliacoesScore; this.tempoResposta = tempoResposta;
        this.verificado = verificado; this.disponivel = disponivel; this.precoBase = precoBase;
        this.distanciaKm = distanciaKm; this.precoEstimado = precoEstimado;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecialidade() { return especialidade; }
    public String getCategoria() { return categoria; }
    public int getScore() { return score; }
    public int getFormacao() { return formacao; }
    public int getCertificacoes() { return certificacoes; }
    public int getAvaliacoesScore() { return avaliacoesScore; }
    public int getTempoResposta() { return tempoResposta; }
    public boolean isVerificado() { return verificado; }
    public boolean isDisponivel() { return disponivel; }
    public BigDecimal getPrecoBase() { return precoBase; }
    public double getDistanciaKm() { return distanciaKm; }
    public BigDecimal getPrecoEstimado() { return precoEstimado; }
}
