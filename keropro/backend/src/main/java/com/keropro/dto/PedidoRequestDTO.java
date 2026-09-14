package com.keropro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PedidoRequestDTO {

    @NotNull
    private Long clienteId;

    @NotNull
    private Long profissionalId;

    @NotBlank
    private String descricao;

    @NotNull
    private Double clienteLatitude;

    @NotNull
    private Double clienteLongitude;

    private boolean emergencia;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Long getProfissionalId() { return profissionalId; }
    public void setProfissionalId(Long profissionalId) { this.profissionalId = profissionalId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Double getClienteLatitude() { return clienteLatitude; }
    public void setClienteLatitude(Double clienteLatitude) { this.clienteLatitude = clienteLatitude; }
    public Double getClienteLongitude() { return clienteLongitude; }
    public void setClienteLongitude(Double clienteLongitude) { this.clienteLongitude = clienteLongitude; }
    public boolean isEmergencia() { return emergencia; }
    public void setEmergencia(boolean emergencia) { this.emergencia = emergencia; }
}
