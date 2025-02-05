package Cliente;

public class ExcluirUsuario {
    private String operacao;
    private String token;
    private String ra;

    public ExcluirUsuario(String token, String ra) {
        this.operacao = "excluirUsuario";
        this.ra = ra;
        this.setToken(token);
    }

    public String getOperacao() {
        return operacao;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
