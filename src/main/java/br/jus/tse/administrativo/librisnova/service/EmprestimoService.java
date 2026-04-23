package br.jus.tse.administrativo.librisnova.service;


import br.jus.tse.administrativo.librisnova.entity.*;
import br.jus.tse.administrativo.librisnova.repository.EmprestimoRepository;
import br.jus.tse.administrativo.librisnova.repository.LivroRepository;
import br.jus.tse.administrativo.librisnova.repository.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;


@Service
public class EmprestimoService {
    private final static Logger LOGGER = LoggerFactory.getLogger(EmprestimoService.class);

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, 
                           LivroRepository livroRepository, 
                           UsuarioRepository usuarioRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }



    @Transactional
    public Emprestimo realizarEmprestimo(Long usuarioId, Long livroId) {
        LOGGER.info("Solicitação de empréstimo: Usuário ID {} solicitando Livro ID {}", usuarioId, livroId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Livro livro = livroRepository.findById(livroId)
            .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        if (livro.getStatus() != StatusLivro.DISPONIVEL)  {
            LOGGER.warn("Solicitação negada: Livro ID {} já está com status {}", livroId, livro.getStatus());
            throw new RuntimeException("O livro não está disponível para empréstimo");
        }

        livro.setStatus(StatusLivro.SOLICITADO);
        livro.setUsuarioSolicitanteId(usuarioId); 
        livroRepository.save(livro);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(LocalDate.now()); 
        emprestimo.setStatus(StatusEmprestimo.PENDENTE);

        LOGGER.info("Empréstimo ID {} criado com status PENDENTE", emprestimo.getId());
        return emprestimoRepository.save(emprestimo);

       
    }

    @Transactional
    public Emprestimo aprovarEmprestimo(Long emprestimoId, Usuario aprovador) {
        LOGGER.info("Aprovador ID {} tentando aprovar Empréstimo ID {}", aprovador.getId(), emprestimoId);

        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
            .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        if (emprestimo.getStatus() != StatusEmprestimo.PENDENTE) {
            throw new RuntimeException("Este empréstimo não está pendente");
        }

        emprestimo.setStatus(StatusEmprestimo.ATIVO);
        emprestimo.setAprovador(aprovador);

        Livro livro = emprestimo.getLivro();
        livro.setStatus(StatusLivro.EMPRESTADO);
        livro.setUsuarioSolicitanteId(null);

        LOGGER.info("Empréstimo ID {} APROVADO por {}. Livro ID {} agora está EMPRESTADO", emprestimoId, aprovador.getNome(), emprestimo.getLivro().getId());

        return emprestimoRepository.save(emprestimo);
    }

    public List<Emprestimo> listarPendentes() {
        return emprestimoRepository.findByStatus(StatusEmprestimo.PENDENTE);
    }

    public List<Emprestimo> listarAguardandoDevolucao() {
        return emprestimoRepository.findByStatus(StatusEmprestimo.AGUARDANDO_CONFIRMACAO);
    }

   


    @Transactional
    public void recusarEmprestimo(Long emprestimoId) {

        LOGGER.warn("Recusando e excluindo solicitação de empréstimo ID {}", emprestimoId);

        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
            .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

       

    
        Livro livro = emprestimo.getLivro();
        livro.setStatus(StatusLivro.DISPONIVEL);
        livro.setUsuarioSolicitanteId(null);
        emprestimoRepository.save(emprestimo);

        emprestimoRepository.delete(emprestimo);
    }

    @Transactional
    public Emprestimo realizarDevolucao(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
            .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        emprestimo.setStatus(StatusEmprestimo.AGUARDANDO_CONFIRMACAO);

        

        Livro livro = emprestimo.getLivro();
        livro.setStatus(StatusLivro.EM_DEVOLUCAO);

        livroRepository.save(livro);
         return emprestimoRepository.save(emprestimo);


    }

    @Transactional
    public void confirmarDevolucao(Long emprestimoId, Usuario recebedor) {
        LOGGER.info("Confirmando devolução do empréstimo ID {}. Recebedor: {}", emprestimoId, recebedor.getNome());
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
            .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        emprestimo.setStatus(StatusEmprestimo.FINALIZADO);
        emprestimo.setDataDevolucao(LocalDate.now());
        emprestimo.setRecebedor(recebedor);

        Livro livro = emprestimo.getLivro();
        livro.setStatus(StatusLivro.DISPONIVEL);

        livroRepository.save(livro);
        emprestimoRepository.save(emprestimo);

        LOGGER.info("Empréstimo ID {} FINALIZADO. Livro ID {} retornou ao status DISPONIVEL", emprestimoId, emprestimo.getLivro().getId());
    }

    public List<Emprestimo> listarPorUsuario(Usuario usuarioLogado) {
        if (usuarioLogado.getPerfil() == Perfil.ADMIN || usuarioLogado.getPerfil() == Perfil.APOIO) {
            return emprestimoRepository.findAll(); 
        } else {
            return emprestimoRepository.findByUsuario(usuarioLogado); 
        }
    }

   

    public List<Emprestimo> listarTodos() {
        return emprestimoRepository.findAll();

    }

    
}