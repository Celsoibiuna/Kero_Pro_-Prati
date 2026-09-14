package com.keropro.repository;

import com.keropro.model.Pedido;
import com.keropro.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByProfissional_IdAndStatus(Long profissionalId, StatusPedido status);

    List<Pedido> findByCliente_IdOrderByCriadoEmDesc(Long clienteId);
}
