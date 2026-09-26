package com.keropro.dto;

import java.math.BigDecimal;
import java.time.Instant;

/** Publicado em /topico/pedidos/{id}/localizacao — posição atual do profissional para o mapa de acompanhamento. */
public class LocalizacaoEventoDTO {
    private Long pedidoId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Instant atualizadoEm;

    public LocalizacaoEventoDTO(Long pedidoId, BigDecimal latitude, BigDecimal longitude, Instant atualizadoEm) {
        this.pedidoId = pedidoId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.atualizadoEm = atualizadoEm;
    }

    public Long getPedidoId() { return pedidoId; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public Instant getAtualizadoEm() { return atualizadoEm; }
}
