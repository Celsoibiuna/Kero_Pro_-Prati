package com.keropro.service;

import com.keropro.dto.LocalizacaoEventoDTO;
import com.keropro.dto.PedidoStatusEventoDTO;
import com.keropro.model.Pedido;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Publica os eventos de tempo real do pedido via STOMP/WebSocket — substitui, no
 * mesmo espírito da proposta original (Cloud Firestore), a sincronização instantânea
 * de status e a posição do profissional no mapa de acompanhamento, aqui sobre o
 * mesmo stack Spring Boot + MySQL já usado no restante da API.
 */
@Service
public class PedidoEventoService {

    private final SimpMessagingTemplate messagingTemplate;

    public PedidoEventoService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publicarStatus(Pedido pedido) {
        messagingTemplate.convertAndSend(
                "/topico/pedidos/" + pedido.getId() + "/status",
                new PedidoStatusEventoDTO(pedido.getId(), pedido.getStatus().name()));
    }

    public void publicarLocalizacao(Long pedidoId, BigDecimal latitude, BigDecimal longitude) {
        messagingTemplate.convertAndSend(
                "/topico/pedidos/" + pedidoId + "/localizacao",
                new LocalizacaoEventoDTO(pedidoId, latitude, longitude, Instant.now()));
    }
}
