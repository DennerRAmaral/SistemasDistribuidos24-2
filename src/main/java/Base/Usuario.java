package Base;

public class Usuario {
    private String ra;
    private String nome;
    private String senha;

    public Usuario (String RA, String Nome, String senha){
        this.ra = RA;
        this.nome = Nome;
        this.senha = senha;
    }

    public String getRA() {
        return ra;
    }

    private void setRA(String RA) {
        this.ra = RA;
    }

    public String getNome() {
        return nome;
    }

    private void setNome(String nome) {
        nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    private void setSenha(String senha) {
        this.senha = senha;
    }
}
