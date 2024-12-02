package Cliente;

public class Logout {
    private final String operacao;
    private String token;

    public Logout (String token){
        this.operacao = "logout";
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
