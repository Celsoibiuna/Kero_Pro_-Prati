package com.keropro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da API REST do KeroPro.
 * Sobe um servidor embutido que expõe os endpoints em /api/**,
 * consumidos pelo front-end React.
 */
@SpringBootApplication
public class KeroproApplication {
    public static void main(String[] args) {
        SpringApplication.run(KeroproApplication.class, args);
    }
}
