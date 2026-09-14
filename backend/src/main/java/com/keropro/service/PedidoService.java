package com.keropro.service;

import com.keropro.model.StatusPedido;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

/**
 * Máquina de estados do pedido — garante que o status só avance na ordem correta,
 * espelhando a "Sincronização Real-time" descrita nos requisitos do KeroPro
 * (Cloud Firestore no protótipo original, aqui feita via polling/REST + MySQL).
 */
@Service
public class PedidoService {

    private static final Map<StatusPedido, StatusPedido> PROXIMO_STATUS = new EnumMap<>(StatusPedido.class);
    static {
        PROXIMO_STATUS.put(StatusPedido.PENDENTE, StatusPedido.ACEITO);
        PROXIMO_STATUS.put(StatusPedido.ACEITO, StatusPedido.A_CAMINHO);
        PROXIMO_STATUS.put(StatusPedido.A_CAMINHO, StatusPedido.EM_EXECUCAO);
        PROXIMO_STATUS.put(StatusPedido.EM_EXECUCAO, StatusPedido.CONCLUIDO);
    }

    /** Retorna o próximo status válido, ou o status atual se já estiver concluído. */
    public StatusPedido avancar(StatusPedido atual) {
        return PROXIMO_STATUS.getOrDefault(atual, StatusPedido.CONCLUIDO);
    }

    public boolean podeAvancar(StatusPedido atual) {
        return atual != StatusPedido.CONCLUIDO;
    }
}
