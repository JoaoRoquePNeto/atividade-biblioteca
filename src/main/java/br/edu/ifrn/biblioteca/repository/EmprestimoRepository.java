package br.edu.ifrn.biblioteca.repository;

import br.edu.ifrn.biblioteca.model.Emprestimo;
import br.edu.ifrn.biblioteca.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    // Consulta 4: emprestimos ativos de um usuario, incluindo os livros.
    @EntityGraph(attributePaths = {"usuario", "itens", "itens.livro"})
    List<Emprestimo> findDistinctByUsuarioIdAndStatusOrderByDataEmprestimoDesc(
            Long usuarioId,
            StatusEmprestimo status
    );

    // Consulta 6: emprestimos ativos cuja devolucao ja deveria ter ocorrido.
    @EntityGraph(attributePaths = {"usuario", "itens", "itens.livro"})
    List<Emprestimo> findDistinctByDataDevolucaoPrevistaBeforeAndStatusOrderByDataDevolucaoPrevistaAsc(
            LocalDate dataAtual,
            StatusEmprestimo status
    );
}

