package Cliente;

public class LocalizarUsuario {
    private String operacao;
    private String token;
    private String ra;

    public LocalizarUsuario(String token, String ra) {
        this.operacao = "localizarUsuario";
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
