package com.keropro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class CadastroClienteDTO {

    @NotBlank(message = "Informe seu nome completo")
    private String nome;

    @NotBlank @Email(message = "E-mail inválido")
    private String email;

    @NotBlank @Size(min = 8, message = "A senha deve ter ao menos 8 caracteres")
    private String senha;

    @NotBlank @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    private String cpf;

    @NotBlank @Pattern(regexp = "\\d{10,11}", message = "Telefone inválido")
    private String telefone;

    @NotNull
    private String dataNascimento; // yyyy-MM-dd

    @NotNull @Valid
    private EnderecoDTO endereco;

    @AssertTrue(message = "É necessário aceitar os Termos de Uso")
    private boolean aceitouTermos;

    @AssertTrue(message = "É necessário aceitar o tratamento de dados (LGPD)")
    private boolean aceitouLgpd;

    private boolean aceitouMarketing;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }
    public EnderecoDTO getEndereco() { return endereco; }
    public void setEndereco(EnderecoDTO endereco) { this.endereco = endereco; }
    public boolean isAceitouTermos() { return aceitouTermos; }
    public void setAceitouTermos(boolean aceitouTermos) { this.aceitouTermos = aceitouTermos; }
    public boolean isAceitouLgpd() { return aceitouLgpd; }
    public void setAceitouLgpd(boolean aceitouLgpd) { this.aceitouLgpd = aceitouLgpd; }
    public boolean isAceitouMarketing() { return aceitouMarketing; }
    public void setAceitouMarketing(boolean aceitouMarketing) { this.aceitouMarketing = aceitouMarketing; }
}
