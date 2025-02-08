package Cliente;

public class LocalizarCategoria {
    private String operacao;
    private String token;
    private int id;

    public LocalizarCategoria(String token, int id) {
        this.operacao = "localizarCategoria";
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
