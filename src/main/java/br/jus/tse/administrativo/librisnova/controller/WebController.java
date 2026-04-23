package br.jus.tse.administrativo.librisnova.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import br.jus.tse.administrativo.librisnova.dto.NovoUsuarioRequest;


import org.springframework.ui.Model;

@Controller
public class WebController {


    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("usuario", new NovoUsuarioRequest(null, "", "", "", null));
        return "register";
    }

    @GetMapping("/")
    public String home(){
        return "index";
    }


    



    




  

    
}
