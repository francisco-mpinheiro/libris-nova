package br.jus.tse.administrativo.librisnova.dto;

import br.jus.tse.administrativo.librisnova.entity.Perfil;
import br.jus.tse.administrativo.librisnova.entity.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @NotNull
    @Size(min=3, max=100)
    String nome, 

    @NotNull
    @Email
    String email,


    String senha, 
    
    Perfil perfil) {
                 
    public Usuario toEntity() {
        return new Usuario.Builder()
                .nome(this.nome)
                .email(this.email)
                .senha(this.senha)
                .perfil(this.perfil)
                .build();
    }        


}
