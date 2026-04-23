package br.jus.tse.administrativo.librisnova.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import br.jus.tse.administrativo.librisnova.dto.UserRequest;
import br.jus.tse.administrativo.librisnova.entity.Emprestimo;
import br.jus.tse.administrativo.librisnova.entity.Usuario;
import br.jus.tse.administrativo.librisnova.service.EmprestimoService;
import br.jus.tse.administrativo.librisnova.service.UsuarioService;

@Controller
public class AdminController {

    private final UsuarioService service;

   private final EmprestimoService emprestimoService;

    public AdminController(UsuarioService service, EmprestimoService emprestimoService) {
        this.service = service;
        this.emprestimoService = emprestimoService;
    }

    @GetMapping("/admin/usuarios-page")
    public String adminUsuarios() {
        return "adm";
    }

    

    @GetMapping("/admin/emprestimos-page")
    public String emprestimosPage(Model model) {

        List<Emprestimo> emprestimosPendentes = emprestimoService.listarPendentes();

        List<Emprestimo> emprestimosAguardandoDevolucao = emprestimoService.listarAguardandoDevolucao();


        model.addAttribute("solicitacoes", emprestimosPendentes);
        model.addAttribute("devolucoes", emprestimosAguardandoDevolucao);

        model.addAttribute("todosEmprestimos", emprestimoService.listarTodos());

        return "emprestimos";
    }

   

    @GetMapping("/admin/usuarios")
    @ResponseBody
    public List<Usuario> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/admin/usuarios/{id}")
    @ResponseBody
    public ResponseEntity<Usuario> buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/usuarios")
    @ResponseBody
    public ResponseEntity<Usuario> cadastrarPorAdm(@RequestBody UserRequest request) {
        return ResponseEntity.ok(service.salvar(request.toEntity()));
    }

    @PutMapping("/admin/usuarios/{id}")
    @ResponseBody
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody UserRequest request) {
        Usuario usuarioParaAtualizar = request.toEntity();
        return ResponseEntity.ok(service.atualizar(id, usuarioParaAtualizar));
    }

    @DeleteMapping("/admin/usuarios/{id}")
    @ResponseBody
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

}