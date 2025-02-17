package Cliente;

public class ListarUserCateg {
    private String operacao;
    private String token;
    private String ra;

    public ListarUserCateg(String token, String ra) {
        this.operacao = "listarUsuarioCategorias";
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
