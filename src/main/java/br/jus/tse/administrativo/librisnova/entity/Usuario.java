package br.jus.tse.administrativo.librisnova.entity;
 
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.List;
 
@Entity
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    
    @Column(unique = true) 
    private String email;
    private String senha;
 
    @Enumerated(EnumType.STRING)
    private Perfil perfil; 
 
    
    public Usuario() {}
 
    public Usuario(Builder builder) {
        this.id = builder.id;
        this.nome = builder.nome;
        this.email = builder.email;
        this.senha = builder.senha;
        this.perfil = builder.perfil;
    }

    public static class Builder{
        private Long id;
        private String nome;
        private String email;
        private String senha;
        private Perfil perfil;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder senha(String senha) {
            this.senha = senha;
            return this;
        }

        public Builder perfil(Perfil perfil) {
            this.perfil = perfil;
            return this;
        }

        public Usuario build() {
            return new Usuario(this);
        }

    }
 

 
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.perfil == Perfil.ADMIN) {
            return List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_APOIO"),
                new SimpleGrantedAuthority("ROLE_USUARIO")
            );
        }
        if (this.perfil == Perfil.APOIO) {
            return List.of(
                new SimpleGrantedAuthority("ROLE_APOIO"),
                new SimpleGrantedAuthority("ROLE_USUARIO")
            );
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USUARIO"));
    }
 
    @Override
    public String getPassword() {
        return senha;
    }
 
    @Override
    public String getUsername() {
        return email;
    }
 
    @Override
    public boolean isAccountNonExpired() { return true; }
 
    @Override
    public boolean isAccountNonLocked() { return true; }
 
    @Override
    public boolean isCredentialsNonExpired() { return true; } 
 
    @Override
    public boolean isEnabled() { return true; }
 
   
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
}