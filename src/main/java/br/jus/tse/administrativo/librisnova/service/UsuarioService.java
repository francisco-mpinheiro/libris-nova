package br.jus.tse.administrativo.librisnova.service;

import br.jus.tse.administrativo.librisnova.entity.Usuario;
import br.jus.tse.administrativo.librisnova.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class UsuarioService {
    private final static Logger LOGGER = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder; 

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvar(Usuario usuario) {

        LOGGER.info("Cadastrando usuário: {}");
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        LOGGER.info("Usuário {} cadastrado com sucesso: {}", usuario.getEmail(), usuario.getId());
        return repository.save(usuario);

    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repository.findById(id);
    }
 
    public Usuario atualizar(Long id, Usuario usuario) {
        return repository.findById(id).map(usuarioExistente -> {
            LOGGER.info("Atualizando dados do usuário ID: {}", id);
            usuario.setId(id);
            
            
            if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            } 
            else {
                usuario.setSenha(usuarioExistente.getSenha());
            }
            
            return repository.save(usuario);
        }).orElseThrow(() -> {
            LOGGER.error("Falha ao atualizar: Usuário com ID {} não encontrado", id);
           return new RuntimeException("Usuário não encontrado com ID: " + id);
                });
    }
 
    public void deletar(Long id) {
        if (repository.existsById(id)) {
            LOGGER.warn("Excluindo permanentemente o usuário ID: {}", id);
            repository.deleteById(id);
        } else {
            LOGGER.error("Tentativa de exclusão falhou: ID {} não existe", id);
        }
    }
}