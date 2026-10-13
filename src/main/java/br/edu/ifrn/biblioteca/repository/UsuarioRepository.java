package br.edu.ifrn.biblioteca.repository;

import br.edu.ifrn.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Consulta 3: busca parcial pelo nome, sem diferenciar maiusculas e minusculas.
    List<Usuario> findByNomeContainingIgnoreCaseOrderByNomeAsc(String parteDoNome);
}

