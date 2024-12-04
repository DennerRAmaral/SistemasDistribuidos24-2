package Base;

public class Usuario {
    private int ra;
    private String nome;
    private String senha;

    public Usuario(int RA, String Nome, String senha) {
        this.ra = RA;
        this.nome = Nome;
        this.senha = senha;
    }

    public int getRA() {
        return ra;
    }

    private void setRA(int RA) {
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
