package com.keropro.dto;

/** Publicado em /topico/pedidos/{id}/status a cada mudança de status — sincronização em tempo real. */
public class PedidoStatusEventoDTO {
    private Long pedidoId;
    private String status;

    public PedidoStatusEventoDTO(Long pedidoId, String status) {
        this.pedidoId = pedidoId;
        this.status = status;
    }

    public Long getPedidoId() { return pedidoId; }
    public String getStatus() { return status; }
}
