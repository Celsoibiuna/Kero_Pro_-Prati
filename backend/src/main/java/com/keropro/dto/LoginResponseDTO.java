package com.keropro.dto;

public class LoginResponseDTO {
    private Long id;
    private String nome;
    private String tipo; // CLIENTE | PROFISSIONAL
    private String token; // token fictício de sessão (substituir por JWT em produção)

    public LoginResponseDTO(Long id, String nome, String tipo, String token) {
        this.id = id; this.nome = nome; this.tipo = tipo; this.token = token;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public String getToken() { return token; }
}
