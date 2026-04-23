package br.jus.tse.administrativo.librisnova.controller;

import br.jus.tse.administrativo.librisnova.dto.UserRequest;
import br.jus.tse.administrativo.librisnova.entity.Usuario;
import br.jus.tse.administrativo.librisnova.service.UsuarioService;


import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

        private final UsuarioService service;

        public UsuarioController(UsuarioService service) {
            this.service = service;
        }

    
        @PostMapping
        public Usuario criar(@RequestBody UserRequest usuario) {
            System.out.println(usuario);
            return service.salvar(usuario.toEntity());
        }

      

    
        

}
