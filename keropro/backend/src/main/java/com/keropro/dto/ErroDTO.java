package com.keropro.dto;

/** Formato padrão de erro devolvido pela API — o front-end lê o campo "mensagem". */
public class ErroDTO {
    private String mensagem;
    public ErroDTO(String mensagem) { this.mensagem = mensagem; }
    public String getMensagem() { return mensagem; }
}
