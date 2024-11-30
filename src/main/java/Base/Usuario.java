package Base;

public class Usuario {
    private String RA;
    private String Nome;
    private String senha;

    public Usuario (String RA, String Nome, String senha){
        this.RA = RA;
        this.Nome = Nome;
        this.senha = senha;
    }

    public String getRA() {
        return RA;
    }

    private void setRA(String RA) {
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
