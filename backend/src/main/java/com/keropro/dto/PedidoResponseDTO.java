package com.keropro.dto;

import java.math.BigDecimal;

public class PedidoResponseDTO {
    private Long id;
    private String clienteNome;
    private String profissionalNome;
    private String descricao;
    private String status;
    private BigDecimal distanciaKm;
    private BigDecimal valorEstimado;
    private boolean emergencia;

    public PedidoResponseDTO(Long id, String clienteNome, String profissionalNome, String descricao,
                              String status, BigDecimal distanciaKm, BigDecimal valorEstimado, boolean emergencia) {
        this.id = id; this.clienteNome = clienteNome; this.profissionalNome = profissionalNome;
        this.descricao = descricao; this.status = status; this.distanciaKm = distanciaKm;
        this.valorEstimado = valorEstimado; this.emergencia = emergencia;
    }
    public Long getId() { return id; }
    public String getClienteNome() { return clienteNome; }
    public String getProfissionalNome() { return profissionalNome; }
    public String getDescricao() { return descricao; }
    public String getStatus() { return status; }
    public BigDecimal getDistanciaKm() { return distanciaKm; }
    public BigDecimal getValorEstimado() { return valorEstimado; }
    public boolean isEmergencia() { return emergencia; }
}
