package com.keropro.repository;

import com.keropro.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    List<Profissional> findByCategoria_SlugAndDisponivelTrue(String slug);

    List<Profissional> findByDisponivelTrue();
}
