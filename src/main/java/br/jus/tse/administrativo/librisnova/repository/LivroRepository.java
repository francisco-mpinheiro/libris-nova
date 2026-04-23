package br.jus.tse.administrativo.librisnova.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.jus.tse.administrativo.librisnova.entity.Livro;
import br.jus.tse.administrativo.librisnova.entity.StatusLivro;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long>{

    List<Livro> findAllByStatus(StatusLivro disponivel);

}
