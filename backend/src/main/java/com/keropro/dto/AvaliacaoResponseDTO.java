package com.keropro.dto;

public class AvaliacaoResponseDTO {
    private Long id;
    private int nota;
    private String comentario;
    private int novoScoreProfissional;

    public AvaliacaoResponseDTO(Long id, int nota, String comentario, int novoScoreProfissional) {
        this.id = id;
        this.nota = nota;
        this.comentario = comentario;
        this.novoScoreProfissional = novoScoreProfissional;
    }

    public Long getId() { return id; }
    public int getNota() { return nota; }
    public String getComentario() { return comentario; }
    public int getNovoScoreProfissional() { return novoScoreProfissional; }
}
