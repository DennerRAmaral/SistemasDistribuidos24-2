package Cliente;

public class ExcluirCategoria {
    private String operacao;
    private String token;
    private int id;

    public ExcluirCategoria(String token, int id) {
        this.operacao = "excluirCategoria";
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
