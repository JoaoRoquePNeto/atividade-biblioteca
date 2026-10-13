package br.edu.ifrn.biblioteca.repository;

import br.edu.ifrn.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {

    // Consulta 1: livros com pelo menos uma unidade disponivel.
    List<Livro> findByQuantidadeDisponivelGreaterThanOrderByTituloAsc(Integer quantidade);

    // Consulta 2: livros pelo nome da categoria.
    List<Livro> findByCategoriaNomeIgnoreCaseOrderByTituloAsc(String nomeCategoria);

    // Consulta 5: livros pelo nome do autor.
    List<Livro> findDistinctByAutoresNomeIgnoreCaseOrderByAnoPublicacaoAsc(String nomeAutor);
}

