package com.keropro.controller;

import com.keropro.model.Categoria;
import com.keropro.repository.CategoriaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping("/api/categorias")
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }
}
