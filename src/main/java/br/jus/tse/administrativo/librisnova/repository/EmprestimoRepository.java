package br.jus.tse.administrativo.librisnova.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import br.jus.tse.administrativo.librisnova.entity.Emprestimo;
import br.jus.tse.administrativo.librisnova.entity.StatusEmprestimo;
import br.jus.tse.administrativo.librisnova.entity.Usuario;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long>{
    List<Emprestimo> findByUsuario(Usuario usuario); 
    List<Emprestimo> findByStatus(StatusEmprestimo status);
    
}

