package br.jus.tse.administrativo.librisnova.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import br.jus.tse.administrativo.librisnova.dto.NovoUsuarioRequest;
import br.jus.tse.administrativo.librisnova.entity.Usuario;
import br.jus.tse.administrativo.librisnova.repository.UsuarioRepository;

@Controller
public class ProfileController {

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/profile/usuarios")
    public String profileUsuarios(Model model, Principal principal){
        model.addAttribute("usuario", new NovoUsuarioRequest(null, null, null, null, null));

        String email = principal.getName();

        Usuario user = usuarioRepository.findUsuarioByEmail(email);

        model.addAttribute("usuario", user);
        return "profile";
    }
    @PostMapping("/profile/atualizar")
    public String atualizarUsuario(@ModelAttribute("usuario") Usuario usuarioAlterado, Principal principal) {


        Usuario usuarioNoBanco = usuarioRepository.findUsuarioByEmail(principal.getName());

        if (usuarioNoBanco == null) {
            return "redirect:/login";
        }


        usuarioNoBanco.setNome(usuarioAlterado.getNome());
        String emailAntigo = usuarioNoBanco.getEmail();
        usuarioNoBanco.setEmail(usuarioAlterado.getEmail());


        if (usuarioAlterado.getSenha() != null && !usuarioAlterado.getSenha().trim().isEmpty()) {
            String senhaCriptografada = passwordEncoder.encode(usuarioAlterado.getSenha().trim());
            usuarioNoBanco.setSenha(senhaCriptografada);
        }

        usuarioRepository.save(usuarioNoBanco);

        if (!emailAntigo.equals(usuarioAlterado.getEmail())) {
            UsernamePasswordAuthenticationToken novaAuth = new UsernamePasswordAuthenticationToken(
                    usuarioNoBanco,
                    usuarioNoBanco.getSenha(),
                    usuarioNoBanco.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(novaAuth);
        }

        return "redirect:/profile/usuarios?sucesso";
    }
}
