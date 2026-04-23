package br.jus.tse.administrativo.librisnova.entity;

public enum Perfil {

    ADMIN("admin"),
    APOIO("apoio"),
    USUARIO("usuario");

    private final String role;

    Perfil(String role){
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
