package com.keropro.model;

/** Estados do ciclo de vida de um pedido, refletidos em tempo real no app do cliente e do profissional. */
public enum StatusPedido {
    PENDENTE,
    ACEITO,
    A_CAMINHO,
    EM_EXECUCAO,
    CONCLUIDO
}
