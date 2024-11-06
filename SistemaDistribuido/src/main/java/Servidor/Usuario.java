package Servidor;

public class Usuario {
    private int RA;
    private String Nome;
    private String senha;

    public Usuario(int RA, String Nome, String senha){
        this.RA = RA;
        this.Nome = Nome;
        this.senha = senha;
    }

    public int getRA() {
        return RA;
    }

    private void setRA(int RA) {
        this.RA = RA;
    }

    public String getNome() {
        return Nome;
    }

    private void setNome(String nome) {
        Nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    private void setSenha(String senha) {
        this.senha = senha;
    }
}
