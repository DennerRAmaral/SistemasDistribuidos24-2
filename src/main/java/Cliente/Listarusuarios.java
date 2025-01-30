package Cliente;

public class Listarusuarios {
    private final String operacao;
    private String token;

    public Listarusuarios (String token){
        this.operacao = "listarUsuarios";
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
