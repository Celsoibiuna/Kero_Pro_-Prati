package com.keropro.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "profissionais")
public class Profissional {

    @Id
    private Long id; // compartilha o PK com usuarios

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false, length = 120)
    private String especialidade;

    @Column(name = "anos_experiencia", nullable = false)
    private int anosExperiencia;

    @Column(name = "raio_atendimento_km", nullable = false)
    private int raioAtendimentoKm = 10;

    @Column(length = 600)
    private String bio;

    @Column(name = "instituicao_formacao", nullable = false, length = 160)
    private String instituicaoFormacao;

    @Column(name = "curso_formacao", nullable = false, length = 160)
    private String cursoFormacao;

    @Column(name = "ano_conclusao", nullable = false)
    private int anoConclusao;

    @Column(name = "certificacoes_adicionais", length = 400)
    private String certificacoesAdicionais;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "preco_base", nullable = false, precision = 8, scale = 2)
    private BigDecimal precoBase;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_verificacao", nullable = false, length = 20)
    private StatusVerificacao statusVerificacao = StatusVerificacao.PENDENTE;

    @Column(nullable = false)
    private boolean verificado;

    @Column(nullable = false)
    private boolean disponivel = true;

    @OneToOne(mappedBy = "profissional", cascade = CascadeType.ALL)
    private ScoreProfissional score;

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; this.id = usuario != null ? usuario.getId() : null; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public int getAnosExperiencia() { return anosExperiencia; }
    public void setAnosExperiencia(int anosExperiencia) { this.anosExperiencia = anosExperiencia; }
    public int getRaioAtendimentoKm() { return raioAtendimentoKm; }
    public void setRaioAtendimentoKm(int raioAtendimentoKm) { this.raioAtendimentoKm = raioAtendimentoKm; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getInstituicaoFormacao() { return instituicaoFormacao; }
    public void setInstituicaoFormacao(String instituicaoFormacao) { this.instituicaoFormacao = instituicaoFormacao; }
    public String getCursoFormacao() { return cursoFormacao; }
    public void setCursoFormacao(String cursoFormacao) { this.cursoFormacao = cursoFormacao; }
    public int getAnoConclusao() { return anoConclusao; }
    public void setAnoConclusao(int anoConclusao) { this.anoConclusao = anoConclusao; }
    public String getCertificacoesAdicionais() { return certificacoesAdicionais; }
    public void setCertificacoesAdicionais(String certificacoesAdicionais) { this.certificacoesAdicionais = certificacoesAdicionais; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public BigDecimal getPrecoBase() { return precoBase; }
    public void setPrecoBase(BigDecimal precoBase) { this.precoBase = precoBase; }
    public StatusVerificacao getStatusVerificacao() { return statusVerificacao; }
    public void setStatusVerificacao(StatusVerificacao statusVerificacao) { this.statusVerificacao = statusVerificacao; }
    public boolean isVerificado() { return verificado; }
    public void setVerificado(boolean verificado) { this.verificado = verificado; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
    public ScoreProfissional getScore() { return score; }
    public void setScore(ScoreProfissional score) { this.score = score; }
}
