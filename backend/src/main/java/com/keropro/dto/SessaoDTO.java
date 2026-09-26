package com.keropro.dto;

/** Retornado por GET /api/auth/me — confirma para a tela splash que a sessão salva ainda é válida. */
public class SessaoDTO {
    private Long id;
    private String nome;
    private String tipo;

    public SessaoDTO(Long id, String nome, String tipo) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
}
