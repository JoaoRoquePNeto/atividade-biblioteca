package br.edu.ifrn.biblioteca.repository;

import br.edu.ifrn.biblioteca.model.Autor;
import br.edu.ifrn.biblioteca.model.Categoria;
import br.edu.ifrn.biblioteca.model.Emprestimo;
import br.edu.ifrn.biblioteca.model.Livro;
import br.edu.ifrn.biblioteca.model.StatusEmprestimo;
import br.edu.ifrn.biblioteca.model.Usuario;
import br.edu.ifrn.biblioteca.repository.projection.CategoriaQuantidade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ConsultasRepositoryTest {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    private Categoria tecnologia;
    private Categoria literatura;
    private Livro javaBasico;
    private Livro springBoot;
    private Livro romance;
    private Usuario joao;

    @BeforeEach
    void prepararDados() {
        tecnologia = novaCategoria("Tecnologia");
        literatura = novaCategoria("Literatura");
        categoriaRepository.saveAll(List.of(tecnologia, literatura));

        Autor autorJava = novoAutor("Ana Souza");
        Autor autorRomance = novoAutor("Carlos Lima");
        autorRepository.saveAll(List.of(autorJava, autorRomance));

        javaBasico = novoLivro("001", "Java Basico", 2020, 2, tecnologia, autorJava);
        springBoot = novoLivro("002", "Spring Boot", 2023, 1, tecnologia, autorJava);
        romance = novoLivro("003", "Um Romance", 2018, 0, literatura, autorRomance);
        livroRepository.saveAll(List.of(javaBasico, springBoot, romance));

        joao = novoUsuario("Joao Silva", "11111111111", "joao@email.com");
        Usuario maria = novoUsuario("Maria Silva Santos", "22222222222", "maria@email.com");
        usuarioRepository.saveAll(List.of(joao, maria));

        Emprestimo ativo = novoEmprestimo(joao, LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5), StatusEmprestimo.ATIVO, javaBasico);
        Emprestimo atrasado = novoEmprestimo(joao, LocalDate.now().minusDays(15),
                LocalDate.now().minusDays(5), StatusEmprestimo.ATIVO, springBoot);
        Emprestimo devolvido = novoEmprestimo(joao, LocalDate.now().minusDays(20),
                LocalDate.now().minusDays(10), StatusEmprestimo.DEVOLVIDO, romance);
        emprestimoRepository.saveAll(List.of(ativo, atrasado, devolvido));
    }

    @Test
    void deveListarLivrosDisponiveisOrdenadosPorTitulo() {
        List<Livro> resultado = livroRepository
                .findByQuantidadeDisponivelGreaterThanOrderByTituloAsc(0);

        assertThat(resultado)
                .extracting(Livro::getTitulo)
                .containsExactly("Java Basico", "Spring Boot");
    }

    @Test
    void deveBuscarLivrosPorCategoria() {
        List<Livro> resultado = livroRepository
                .findByCategoriaNomeIgnoreCaseOrderByTituloAsc("tecnologia");

        assertThat(resultado)
                .extracting(Livro::getTitulo)
                .containsExactly("Java Basico", "Spring Boot");
    }

    @Test
    void deveBuscarUsuariosPorParteDoNome() {
        List<Usuario> resultado = usuarioRepository
                .findByNomeContainingIgnoreCaseOrderByNomeAsc("silva");

        assertThat(resultado)
                .extracting(Usuario::getNome)
                .containsExactly("Joao Silva", "Maria Silva Santos");
    }

    @Test
    void deveListarEmprestimosAtivosDoUsuarioComOsLivros() {
        List<Emprestimo> resultado = emprestimoRepository
                .findDistinctByUsuarioIdAndStatusOrderByDataEmprestimoDesc(
                        joao.getId(), StatusEmprestimo.ATIVO);

        assertThat(resultado).hasSize(2);
        assertThat(resultado)
                .allSatisfy(emprestimo -> assertThat(emprestimo.getItens()).isNotEmpty());
    }

    @Test
    void deveEncontrarLivrosDoAutorOrdenadosPorAno() {
        List<Livro> resultado = livroRepository
                .findDistinctByAutoresNomeIgnoreCaseOrderByAnoPublicacaoAsc("Ana Souza");

        assertThat(resultado)
                .extracting(Livro::getTitulo)
                .containsExactly("Java Basico", "Spring Boot");
    }

    @Test
    void deveListarSomenteEmprestimosAtivosEEmAtraso() {
        List<Emprestimo> resultado = emprestimoRepository
                .findDistinctByDataDevolucaoPrevistaBeforeAndStatusOrderByDataDevolucaoPrevistaAsc(
                        LocalDate.now(), StatusEmprestimo.ATIVO);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getItens().get(0).getLivro().getTitulo())
                .isEqualTo("Spring Boot");
        assertThat(resultado.get(0).getUsuario().getNome()).isEqualTo("Joao Silva");
    }

    @Test
    void deveContarLivrosPorCategoria() {
        List<CategoriaQuantidade> resultado = categoriaRepository.contarLivrosPorCategoria();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("Tecnologia");
        assertThat(resultado.get(0).getQuantidade()).isEqualTo(2L);
        assertThat(resultado.get(1).getNome()).isEqualTo("Literatura");
        assertThat(resultado.get(1).getQuantidade()).isEqualTo(1L);
    }

    private Categoria novaCategoria(String nome) {
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        return categoria;
    }

    private Autor novoAutor(String nome) {
        Autor autor = new Autor();
        autor.setNome(nome);
        return autor;
    }

    private Livro novoLivro(String isbn, String titulo, Integer ano, Integer quantidadeDisponivel,
                            Categoria categoria, Autor autor) {
        Livro livro = new Livro();
        livro.setIsbn(isbn);
        livro.setTitulo(titulo);
        livro.setAnoPublicacao(ano);
        livro.setQuantidadeTotal(3);
        livro.setQuantidadeDisponivel(quantidadeDisponivel);
        livro.setCategoria(categoria);
        livro.getAutores().add(autor);
        return livro;
    }

    private Usuario novoUsuario(String nome, String cpf, String email) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setDataCadastro(LocalDate.now());
        usuario.setAtivo(true);
        return usuario;
    }

    private Emprestimo novoEmprestimo(Usuario usuario, LocalDate dataEmprestimo,
                                      LocalDate dataPrevista, StatusEmprestimo status, Livro livro) {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setDataEmprestimo(dataEmprestimo);
        emprestimo.setDataDevolucaoPrevista(dataPrevista);
        emprestimo.setStatus(status);
        emprestimo.adicionarLivro(livro);
        return emprestimo;
    }
}

