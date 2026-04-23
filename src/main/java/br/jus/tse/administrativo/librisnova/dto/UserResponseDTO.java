package br.jus.tse.administrativo.librisnova.dto;

import br.jus.tse.administrativo.librisnova.entity.Perfil;

public record UserResponseDTO(Long id, String nome, String email, Perfil perfil) {

}
