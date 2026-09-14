package com.keropro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FormacaoDTO {

    @NotBlank
    private String instituicao;

    @NotBlank
    private String curso;

    @NotNull
    private Integer anoConclusao;

    private String certificacoesAdicionais;

    public String getInstituicao() { return instituicao; }
    public void setInstituicao(String instituicao) { this.instituicao = instituicao; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public Integer getAnoConclusao() { return anoConclusao; }
    public void setAnoConclusao(Integer anoConclusao) { this.anoConclusao = anoConclusao; }
    public String getCertificacoesAdicionais() { return certificacoesAdicionais; }
    public void setCertificacoesAdicionais(String certificacoesAdicionais) { this.certificacoesAdicionais = certificacoesAdicionais; }
}
