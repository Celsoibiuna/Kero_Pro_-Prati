package com.keropro.dto;

public class CadastroResponseDTO {
    private Long id;
    private String nome;
    private String tipo;
    private String status;   // ATIVO (cliente) | PENDENTE_ANALISE (profissional)
    private String mensagem;

    public CadastroResponseDTO(Long id, String nome, String tipo, String status, String mensagem) {
        this.id = id; this.nome = nome; this.tipo = tipo; this.status = status; this.mensagem = mensagem;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public String getStatus() { return status; }
    public String getMensagem() { return mensagem; }
}
