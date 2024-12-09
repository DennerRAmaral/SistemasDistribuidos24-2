package Cliente;

public class Login {
    private final String operacao;
    private String ra;
    private String senha;

    public Login(String ra, String senha) {
        this.operacao = "login";
        this.ra = ra;
        this.setSenha(senha);
    }

    public String getRA() {
        return ra;
    }

    public void setRA(String ra) {
        this.ra = ra;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getOperacao() {
        return operacao;
    }
}
