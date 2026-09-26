package com.keropro.security;

import com.keropro.model.Pedido;
import com.keropro.repository.PedidoRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Autentica o CONNECT do STOMP pelo mesmo JWT usado no REST (header "Authorization"
 * nativo do frame) e restringe cada SUBSCRIBE a /topico/pedidos/{id}/** a quem
 * participa daquele pedido — sem isso, qualquer cliente conectado poderia escutar
 * a localização e o status de pedidos de terceiros.
 */
@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final Pattern DESTINO_PEDIDO = Pattern.compile("^/topico/pedidos/(\\d+)/.+$");

    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;
    private final PedidoRepository pedidoRepository;

    public StompAuthChannelInterceptor(JwtService jwtService, TokenBlacklistService blacklistService,
                                        PedidoRepository pedidoRepository) {
        this.jwtService = jwtService;
        this.blacklistService = blacklistService;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            autenticar(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            autorizarAssinatura(accessor);
        }
        return message;
    }

    private void autenticar(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AccessDeniedException("Autenticação necessária para conectar.");
        }
        try {
            Claims claims = jwtService.validarEExtrairClaims(header.substring(7));
            if (blacklistService.estaRevogado(claims.getId())) {
                throw new AccessDeniedException("Sessão encerrada. Faça login novamente.");
            }
            AuthenticatedUser usuario = new AuthenticatedUser(
                    Long.valueOf(claims.getSubject()),
                    claims.get("nome", String.class),
                    claims.get("tipo", String.class),
                    claims.getId(),
                    claims.getExpiration().toInstant());
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.tipo()));
            accessor.setUser(new UsernamePasswordAuthenticationToken(usuario, null, authorities));
        } catch (JwtException | IllegalArgumentException tokenInvalido) {
            throw new AccessDeniedException("Token inválido ou expirado.");
        }
    }

    private void autorizarAssinatura(StompHeaderAccessor accessor) {
        Authentication auth = (Authentication) accessor.getUser();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUser usuario)) {
            throw new AccessDeniedException("Autenticação necessária para assinar este tópico.");
        }

        String destino = accessor.getDestination();
        Matcher matcher = destino != null ? DESTINO_PEDIDO.matcher(destino) : null;
        if (matcher == null || !matcher.matches()) {
            throw new AccessDeniedException("Tópico inválido.");
        }

        Long pedidoId = Long.valueOf(matcher.group(1));
        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        boolean autorizado = pedido != null && (
                ("CLIENTE".equals(usuario.tipo()) && usuario.id().equals(pedido.getCliente().getId()))
                        || ("PROFISSIONAL".equals(usuario.tipo()) && usuario.id().equals(pedido.getProfissional().getId())));

        if (!autorizado) {
            throw new AccessDeniedException("Você não tem acesso a este pedido.");
        }
    }
}
