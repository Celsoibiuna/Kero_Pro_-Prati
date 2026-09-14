package com.keropro.controller;

import com.keropro.dto.PedidoRequestDTO;
import com.keropro.dto.PedidoResponseDTO;
import com.keropro.model.*;
import com.keropro.repository.PedidoRepository;
import com.keropro.repository.ProfissionalRepository;
import com.keropro.service.OrcamentoService;
import com.keropro.service.PedidoService;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoints de criação e acompanhamento de pedidos (fluxo cliente <-> profissional). */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ProfissionalRepository profissionalRepository;
    private final OrcamentoService orcamentoService;
    private final PedidoService pedidoService;
    private final EntityManager entityManager;

    public PedidoController(PedidoRepository pedidoRepository,
                             ProfissionalRepository profissionalRepository,
                             OrcamentoService orcamentoService,
                             PedidoService pedidoService,
                             EntityManager entityManager) {
        this.pedidoRepository = pedidoRepository;
        this.profissionalRepository = profissionalRepository;
        this.orcamentoService = orcamentoService;
        this.pedidoService = pedidoService;
        this.entityManager = entityManager;
    }

    /** Painel do profissional: pedidos pendentes na região. */
    @GetMapping("/pendentes/{profissionalId}")
    public List<Pedido> pendentes(@PathVariable Long profissionalId) {
        return pedidoRepository.findByProfissional_IdAndStatus(profissionalId, StatusPedido.PENDENTE);
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criar(@Valid @RequestBody PedidoRequestDTO body) {
        Profissional profissional = profissionalRepository.findById(body.getProfissionalId())
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        double distanciaKm = orcamentoService.calcularDistanciaKm(
                body.getClienteLatitude(), body.getClienteLongitude(),
                profissional.getLatitude().doubleValue(), profissional.getLongitude().doubleValue());

        Pedido pedido = new Pedido();
        pedido.setCliente(entityManager.getReference(Cliente.class, body.getClienteId()));
        pedido.setProfissional(profissional);
        pedido.setCategoria(profissional.getCategoria());
        pedido.setDescricao(body.getDescricao());
        pedido.setDistanciaKm(java.math.BigDecimal.valueOf(distanciaKm));
        pedido.setValorEstimado(orcamentoService.calcularPrecoEstimado(
                profissional.getPrecoBase(), distanciaKm, body.isEmergencia()));
        pedido.setEmergencia(body.isEmergencia());
        pedido.setStatus(StatusPedido.PENDENTE);

        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.ok(toDTO(salvo));
    }

    /** Avança o status do pedido em uma etapa (Aceito -> A caminho -> Em execução -> Concluído). */
    @PatchMapping("/{id}/avancar")
    public ResponseEntity<PedidoResponseDTO> avancarStatus(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (pedidoService.podeAvancar(pedido.getStatus())) {
            pedido.setStatus(pedidoService.avancar(pedido.getStatus()));
            pedidoRepository.save(pedido);
        }
        return ResponseEntity.ok(toDTO(pedido));
    }

    private PedidoResponseDTO toDTO(Pedido p) {
        return new PedidoResponseDTO(
                p.getId(),
                p.getCliente().getUsuario() != null ? p.getCliente().getUsuario().getNome() : null,
                p.getProfissional().getUsuario().getNome(),
                p.getDescricao(), p.getStatus().name(),
                p.getDistanciaKm(), p.getValorEstimado(), p.isEmergencia());
    }
}
