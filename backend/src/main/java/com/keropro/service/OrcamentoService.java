package com.keropro.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcula distância (Haversine) a partir das coordenadas de GPS do cliente e do
 * profissional, e converte em um orçamento estimado — funcionalidade "Cálculo de
 * Orçamento" da lista de requisitos do KeroPro.
 */
@Service
public class OrcamentoService {

    private static final double RAIO_TERRA_KM = 6371.0;
    private static final BigDecimal TAXA_POR_KM = new BigDecimal("8.00");
    private static final BigDecimal MULTIPLICADOR_EMERGENCIA = new BigDecimal("1.25");

    /** Distância em quilômetros entre dois pontos de GPS, via fórmula de Haversine. */
    public double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA_KM * c;
    }

    /** Preço estimado = preço base do profissional + (distância x taxa por km), com adicional de emergência. */
    public BigDecimal calcularPrecoEstimado(BigDecimal precoBase, double distanciaKm, boolean emergencia) {
        BigDecimal deslocamento = TAXA_POR_KM.multiply(BigDecimal.valueOf(distanciaKm));
        BigDecimal total = precoBase.add(deslocamento);
        if (emergencia) {
            total = total.multiply(MULTIPLICADOR_EMERGENCIA);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
