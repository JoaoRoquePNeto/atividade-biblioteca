package br.edu.ifrn.biblioteca.repository;

import br.edu.ifrn.biblioteca.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Long> {
}

