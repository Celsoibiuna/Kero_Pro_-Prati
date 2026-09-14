package com.keropro.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    /** Hash BCrypt da senha — nunca armazenar em texto puro. Ver RegisterController + PasswordEncoder. */
    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario tipo;

    @Column(length = 11, unique = true)
    private String cpf;

    @Column(length = 14, unique = true)
    private String cnpj;

    @Column(nullable = false, length = 11)
    private String telefone;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "lgpd_consentimento", nullable = false)
    private boolean lgpdConsentimento;

    @Column(name = "termos_aceitos_em")
    private LocalDateTime termosAceitosEm;

    @Column(name = "aceita_marketing", nullable = false)
    private boolean aceitaMarketing;

    @Column(name = "ultimo_login_em")
    private LocalDateTime ultimoLoginEm;

    @Column(name = "tentativas_login_falhas", nullable = false)
    private int tentativasLoginFalhas;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public boolean isLgpdConsentimento() { return lgpdConsentimento; }
    public void setLgpdConsentimento(boolean lgpdConsentimento) { this.lgpdConsentimento = lgpdConsentimento; }
    public LocalDateTime getTermosAceitosEm() { return termosAceitosEm; }
    public void setTermosAceitosEm(LocalDateTime termosAceitosEm) { this.termosAceitosEm = termosAceitosEm; }
    public boolean isAceitaMarketing() { return aceitaMarketing; }
    public void setAceitaMarketing(boolean aceitaMarketing) { this.aceitaMarketing = aceitaMarketing; }
    public LocalDateTime getUltimoLoginEm() { return ultimoLoginEm; }
    public void setUltimoLoginEm(LocalDateTime ultimoLoginEm) { this.ultimoLoginEm = ultimoLoginEm; }
    public int getTentativasLoginFalhas() { return tentativasLoginFalhas; }
    public void setTentativasLoginFalhas(int tentativasLoginFalhas) { this.tentativasLoginFalhas = tentativasLoginFalhas; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
