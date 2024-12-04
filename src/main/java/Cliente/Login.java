package Cliente;

public class Login {
    private final String operacao;
    private int ra;
    private String senha;

    public Login(int ra, String senha) {
        this.operacao = "login";
        this.ra = ra;
        this.setSenha(senha);
    }

    public int getRA() {
        return ra;
    }

    public void setRA(int ra) {
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
