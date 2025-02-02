package Cliente;

public class Listarusuario {
    private final String operacao;
    private String token;

    public Listarusuario(String token){
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
