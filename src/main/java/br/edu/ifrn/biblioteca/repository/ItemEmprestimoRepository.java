package br.edu.ifrn.biblioteca.repository;

import br.edu.ifrn.biblioteca.model.ItemEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemEmprestimoRepository extends JpaRepository<ItemEmprestimo, Long> {
}

