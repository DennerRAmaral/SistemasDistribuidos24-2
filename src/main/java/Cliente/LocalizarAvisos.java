package Cliente;

public class LocalizarAvisos {
    private String operacao;
    private String token;
    private int id;

    public LocalizarAvisos(String token, int id) {
        this.operacao = "localizarAviso";
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
