package Cliente;

public class ListarCategorias {
    private final String operacao;
    private String token;

    public ListarCategorias(String token){
        this.operacao = "listarCategorias";
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
