package br.jus.tse.administrativo.librisnova;

import br.jus.tse.administrativo.librisnova.entity.Perfil;
import br.jus.tse.administrativo.librisnova.entity.Usuario;
import br.jus.tse.administrativo.librisnova.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ScriptUsers {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {


            criarUsuarioSeNaoExistir(repository, passwordEncoder,
                    "admin@gmail.com", "Admin", "123", Perfil.ADMIN);


            criarUsuarioSeNaoExistir(repository, passwordEncoder,
                    "apoio@gmail.com", "Apoio", "123", Perfil.APOIO);


            criarUsuarioSeNaoExistir(repository, passwordEncoder,
                    "usuario@gmail.com", "Usuario", "123", Perfil.USUARIO);
        };
    }


    private void criarUsuarioSeNaoExistir(UsuarioRepository repository,
                                          PasswordEncoder passwordEncoder,
                                          String email,
                                          String nome,
                                          String senha,
                                          Perfil perfil) {

        if (repository.findByEmail(email) == null) {
            Usuario novoUsuario = new Usuario.Builder()
                    .nome(nome)
                    .email(email)
                    .senha(passwordEncoder.encode(senha))
                    .perfil(perfil)
                    .build();

            repository.save(novoUsuario);
            System.out.println(">>> Usuário criado [" + perfil + "]: " + email);
        } else {
            System.out.println(">>> Usuário " + email + " já existe. Pulando...");
        }
    }
}