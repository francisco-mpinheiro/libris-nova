package br.jus.tse.administrativo.librisnova.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.jus.tse.administrativo.librisnova.entity.Livro;
import br.jus.tse.administrativo.librisnova.entity.Perfil;
import br.jus.tse.administrativo.librisnova.entity.StatusLivro;
import br.jus.tse.administrativo.librisnova.entity.Usuario;
import br.jus.tse.administrativo.librisnova.service.LivroService;

@RestController
@RequestMapping("/livros")
public class LivroController {
        private final LivroService service;

        public LivroController(LivroService service){
            this.service = service;
        }

        @PostMapping
        public Livro criar(@RequestBody Livro livro) {
            return service.salvar(livro);
        }

        @GetMapping
        public List<Livro> listar(@AuthenticationPrincipal UserDetails user){
            Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (usuarioLogado.getPerfil().equals(Perfil.USUARIO)){
                return service.listarTodosDisponiveis();  
            }
            return service.listarTodos();
        }

        @GetMapping("/{id}")
        public ResponseEntity<Livro> buscarPorId(@PathVariable Long id) {
            return service.buscarPorId(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
        }

        @PutMapping("/{id}")
        public ResponseEntity<Livro> atualizar(@PathVariable Long id, @RequestBody Livro livro){
            return service.buscarPorId(id)
                    .map(livroExistente -> {
                        livro.setId(id);
                        return ResponseEntity.ok(service.salvar(livro));
                    })
                    .orElse(ResponseEntity.notFound().build());
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deletar(@PathVariable Long id){
            service.deletar(id);
            return ResponseEntity.noContent().build();

        }
        

        
        
        @PostMapping("/{id}/solicitar")
        public ResponseEntity<?> solicitarLivro(@PathVariable Long id) {
       
        Livro livro = service.buscarPorId(id).orElse(null);

            if (livro == null) return ResponseEntity.notFound().build();
            if (livro.getStatus() != StatusLivro.DISPONIVEL) {
            return ResponseEntity.badRequest().body("Livro indisponível.");
        }

       
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      
        livro.setStatus(StatusLivro.SOLICITADO);
        livro.setUsuarioSolicitanteId(usuarioLogado.getId());
    
            service.salvar(livro);
    
        return ResponseEntity.ok().build();
        }


}
