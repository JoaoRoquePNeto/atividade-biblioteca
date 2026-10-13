# Sistema de Biblioteca

Atividade da disciplina de Desenvolvimento de Sistemas Corporativos.

O projeto usa Spring Boot, Spring Data JPA e o banco H2 em memoria. O foco esta nas
camadas `model` e `repository`, conforme foi solicitado na atividade.

## Requisitos

- Java 17 ou superior
- Maven 3.9 ou superior

## Como executar

Na pasta do projeto, execute:

```bash
mvn spring-boot:run
```

O console do H2 fica disponivel em `http://localhost:8080/h2-console`.

Dados para entrar no H2:

- JDBC URL: `jdbc:h2:mem:biblioteca`
- Usuario: `sa`
- Senha: deixe em branco

## Como executar os testes

```bash
mvn test
```

## Organizacao do projeto

```text
src/main/java/br/edu/ifrn/biblioteca
|-- model
|   |-- Autor.java
|   |-- Categoria.java
|   |-- Emprestimo.java
|   |-- ItemEmprestimo.java
|   |-- Livro.java
|   |-- StatusEmprestimo.java
|   `-- Usuario.java
`-- repository
    |-- AutorRepository.java
    |-- CategoriaRepository.java
    |-- EmprestimoRepository.java
    |-- ItemEmprestimoRepository.java
    |-- LivroRepository.java
    `-- UsuarioRepository.java
```

## Consultas implementadas

### 1. Livros disponiveis

Arquivo: `LivroRepository.java`

```java
findByQuantidadeDisponivelGreaterThanOrderByTituloAsc(0)
```

O valor zero indica que a quantidade disponivel deve ser maior que zero.

### 2. Livros por categoria

Arquivo: `LivroRepository.java`

```java
findByCategoriaNomeIgnoreCaseOrderByTituloAsc(nomeCategoria)
```

O Spring acessa o atributo `nome` que esta dentro de `categoria`.

### 3. Usuarios por parte do nome

Arquivo: `UsuarioRepository.java`

```java
findByNomeContainingIgnoreCaseOrderByNomeAsc(parteDoNome)
```

`Containing` faz uma busca parecida com `LIKE %texto%`.

### 4. Emprestimos ativos de um usuario

Arquivo: `EmprestimoRepository.java`

```java
findDistinctByUsuarioIdAndStatusOrderByDataEmprestimoDesc(usuarioId, StatusEmprestimo.ATIVO)
```

A anotacao `@EntityGraph` carrega tambem os itens e os livros do emprestimo.

### 5. Livros de um autor

Arquivo: `LivroRepository.java`

```java
findDistinctByAutoresNomeIgnoreCaseOrderByAnoPublicacaoAsc(nomeAutor)
```

`Distinct` evita que o mesmo livro apareca repetido.

### 6. Emprestimos atrasados

Arquivo: `EmprestimoRepository.java`

```java
findDistinctByDataDevolucaoPrevistaBeforeAndStatusOrderByDataDevolucaoPrevistaAsc(
    LocalDate.now(), StatusEmprestimo.ATIVO
)
```

`Before` verifica se a data prevista e anterior a data atual.

### 7. Quantidade de livros por categoria

Arquivo: `CategoriaRepository.java`

Esta consulta usa `@Query` porque precisa de `count`, `group by` e ordenacao pela
quantidade. O resultado usa a projecao simples `CategoriaQuantidade`.

## Observacao

O banco H2 foi escolhido para deixar a atividade simples de executar. Quando a aplicacao
e encerrada, os dados do banco em memoria sao apagados.
