package Cliente;

public class ExcluirAviso {
    private String operacao;
    private String token;
    private int id;

    public ExcluirAviso(String token, int id) {
        this.operacao = "excluirAviso";
        this.id = id;
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
