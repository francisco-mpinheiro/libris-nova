package br.jus.tse.administrativo.librisnova.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.jus.tse.administrativo.librisnova.entity.Livro;
import br.jus.tse.administrativo.librisnova.entity.StatusLivro;
import br.jus.tse.administrativo.librisnova.repository.LivroRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LivroService {
    private final static Logger LOGGER = LoggerFactory.getLogger(LivroService.class);

    private final LivroRepository repository;

    public LivroService(LivroRepository repository){
        this.repository = repository;
    }

    public Livro salvar(Livro livro){
        LOGGER.info("Cadastrando/Atualizando livro: Título: '{}'", livro.getTitulo());
        return repository.save(livro);
    }

    public List<Livro> listarTodos(){
        return repository.findAll();

    }

     public List<Livro> listarTodosDisponiveis(){
        Livro livro = new Livro();
        livro.setStatus(StatusLivro.DISPONIVEL);
        return repository.findAllByStatus(StatusLivro.DISPONIVEL);

    }

    public Optional<Livro> buscarPorId(Long id){
        return repository.findById(id);
    }
    
    private boolean existeHistoricoEmprestimo(Long livroId){
        return repository.existsById(livroId);
    }

    public void deletar(Long id){
        Livro livro = repository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Tentativa de exclusão falhou: Livro ID {} não encontrado", id);
                    return new RuntimeException("Livro não encontrado");
                });

        if(!existeHistoricoEmprestimo(id)){
            LOGGER.warn("Exclusão física realizada para o livro ID: {} (Título: '{}')", id, livro.getTitulo());
            repository.deleteById(id);
        } else {
            LOGGER.info("Livro ID: {} possui histórico. Realizando exclusão lógica (Status -> INDISPONIVEL)", id);
            livro.setStatus(StatusLivro.INDISPONIVEL);
            repository.save(livro);
        }
    }


}
