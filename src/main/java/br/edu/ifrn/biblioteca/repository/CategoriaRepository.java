package br.edu.ifrn.biblioteca.repository;

import br.edu.ifrn.biblioteca.model.Categoria;
import br.edu.ifrn.biblioteca.repository.projection.CategoriaQuantidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Consulta 7: conta os livros de cada categoria.
    @Query("""
            select c.nome as nome, count(l) as quantidade
            from Categoria c
            left join c.livros l
            group by c.id, c.nome
            order by count(l) desc
            """)
    List<CategoriaQuantidade> contarLivrosPorCategoria();
}

