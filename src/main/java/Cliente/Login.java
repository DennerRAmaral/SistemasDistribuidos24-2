package Cliente;

public class Login {
    private String RA;
    private String senha;

    public Login (String RA, String senha){
        this.RA = RA;
        this.senha = senha;
    }

    public String getRA() {
        return RA;
    }

    public void setRA(String RA) {
        this.RA = RA;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
