package br.jus.tse.administrativo.librisnova.controller;

import br.jus.tse.administrativo.librisnova.infra.exceptions.EmailInvalidoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import jakarta.validation.Valid; 

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.jus.tse.administrativo.librisnova.dto.NovoUsuarioRequest;
import br.jus.tse.administrativo.librisnova.dto.UserResponseDTO;
import br.jus.tse.administrativo.librisnova.entity.Perfil;
import br.jus.tse.administrativo.librisnova.entity.Usuario;

import br.jus.tse.administrativo.librisnova.repository.UsuarioRepository;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
    
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }




    @PostMapping("/register")
    public String register(@ModelAttribute("usuario") @Valid NovoUsuarioRequest data, 
                BindingResult result,
                Model model,
                RedirectAttributes redirectAttributes ) {

        LOGGER.info("Tentativa de registro de novo usuário com email: {}", data.email());

        if (result.hasErrors()) {
            LOGGER.warn("Erro de validação no cadastro: {}", result.getAllErrors());
            return "register";
        }

        if (repository.findByEmail(data.email()) != null) {
            LOGGER.warn("Falha no cadastro: Email {} já existe.", data.email());
            result.rejectValue("email", "error.usuario", "Este e-mail já está em uso.");
            return "register";
        }


            String senhaCriptografada = passwordEncoder.encode(data.senha());

            Usuario newUsuario = new Usuario.Builder()
                    .nome(data.nome())
                    .email(data.email())
                    .senha(senhaCriptografada)
                    .perfil(data.perfil() != null ? data.perfil() : Perfil.USUARIO)
                    .build();


            repository.save(newUsuario);


            LOGGER.info("Usuário {} cadastrado com sucesso!", data.email());

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Cadastro realizado com sucesso ! Faça login.");
            return "redirect:/login";


    }

    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<UserResponseDTO> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        
        if(auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }

        Usuario user = (Usuario) auth.getPrincipal();
        return ResponseEntity.ok(new UserResponseDTO(
            user.getId(), 
            user.getNome(), 
            user.getEmail(), 
            user.getPerfil()
        
        ));
    }
}
