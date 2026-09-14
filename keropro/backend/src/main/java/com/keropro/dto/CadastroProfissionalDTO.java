package com.keropro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class CadastroProfissionalDTO {

    @NotBlank(message = "Informe seu nome completo")
    private String nome;

    @NotBlank @Email(message = "E-mail inválido")
    private String email;

    @NotBlank @Size(min = 8, message = "A senha deve ter ao menos 8 caracteres")
    private String senha;

    @NotBlank @Pattern(regexp = "CPF|CNPJ", message = "Tipo de documento inválido")
    private String tipoDocumento;

    @NotBlank
    private String documento; // 11 (CPF) ou 14 (CNPJ) dígitos, validado no service

    @NotBlank @Pattern(regexp = "\\d{10,11}", message = "Telefone inválido")
    private String telefone;

    @NotNull
    private String dataNascimento;

    @NotBlank
    private String categoria; // slug: eletrica, hidraulica, ti, clima, reforma

    @NotBlank
    private String especialidade;

    @NotNull @Min(0) @Max(60)
    private Integer anosExperiencia;

    @NotNull @Min(1) @Max(100)
    private Integer raioAtendimentoKm;

    private String bio;

    @NotNull @Valid
    private FormacaoDTO formacao;

    @NotNull @Valid
    private EnderecoDTO endereco;

    private int quantidadeComprovantes;

    @AssertTrue(message = "É necessário aceitar os Termos de Uso")
    private boolean aceitouTermos;

    @AssertTrue(message = "É necessário aceitar o tratamento de dados (LGPD)")
    private boolean aceitouLgpd;

    @AssertTrue(message = "É necessário autorizar a verificação de dados profissionais")
    private boolean aceitouVerificacao;

    private boolean aceitouMarketing;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    public Integer getAnosExperiencia() { return anosExperiencia; }
    public void setAnosExperiencia(Integer anosExperiencia) { this.anosExperiencia = anosExperiencia; }
    public Integer getRaioAtendimentoKm() { return raioAtendimentoKm; }
    public void setRaioAtendimentoKm(Integer raioAtendimentoKm) { this.raioAtendimentoKm = raioAtendimentoKm; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public FormacaoDTO getFormacao() { return formacao; }
    public void setFormacao(FormacaoDTO formacao) { this.formacao = formacao; }
    public EnderecoDTO getEndereco() { return endereco; }
    public void setEndereco(EnderecoDTO endereco) { this.endereco = endereco; }
    public int getQuantidadeComprovantes() { return quantidadeComprovantes; }
    public void setQuantidadeComprovantes(int quantidadeComprovantes) { this.quantidadeComprovantes = quantidadeComprovantes; }
    public boolean isAceitouTermos() { return aceitouTermos; }
    public void setAceitouTermos(boolean aceitouTermos) { this.aceitouTermos = aceitouTermos; }
    public boolean isAceitouLgpd() { return aceitouLgpd; }
    public void setAceitouLgpd(boolean aceitouLgpd) { this.aceitouLgpd = aceitouLgpd; }
    public boolean isAceitouVerificacao() { return aceitouVerificacao; }
    public void setAceitouVerificacao(boolean aceitouVerificacao) { this.aceitouVerificacao = aceitouVerificacao; }
    public boolean isAceitouMarketing() { return aceitouMarketing; }
    public void setAceitouMarketing(boolean aceitouMarketing) { this.aceitouMarketing = aceitouMarketing; }
}
