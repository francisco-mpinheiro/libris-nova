package br.jus.tse.administrativo.librisnova.controller;

import br.jus.tse.administrativo.librisnova.entity.Emprestimo;
import br.jus.tse.administrativo.librisnova.entity.Usuario;
import br.jus.tse.administrativo.librisnova.service.EmprestimoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService service;

    public EmprestimoController(EmprestimoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestParam Long usuarioId, @RequestParam Long livroId) {
        try {
            Emprestimo emprestimo = service.realizarEmprestimo(usuarioId, livroId);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/devolucao")
    public ResponseEntity<?> devolver(@PathVariable Long id) {
        try {
            Emprestimo emprestimo = service.realizarDevolucao(id);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<Emprestimo> listar() {

        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return service.listarPorUsuario(usuarioLogado);
    }

    @GetMapping("/meus-emprestimos")
    public String meusEmprestimos() {
        return "emprestimos";
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovar(@PathVariable Long id) {
        try {
            Usuario aprovador = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Emprestimo emprestimo = service.aprovarEmprestimo(id, aprovador);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/confirmar-devolucao")
    public ResponseEntity<?> confirmar(@PathVariable Long id) {
        try {
            Usuario recebedor = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            service.confirmarDevolucao(id, recebedor);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/recusar")
    public ResponseEntity<?> recusar(@PathVariable Long id) {
        try {
            service.recusarEmprestimo(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}