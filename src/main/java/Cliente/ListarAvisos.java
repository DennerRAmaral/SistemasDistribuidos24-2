package Cliente;

public class ListarAvisos {
    private final String operacao;
    private String token;
    private int categoria;

    public ListarAvisos(String token, int categoria){
        this.operacao = "listarAvisos";
        this.setToken(token);
        this.categoria = categoria;
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

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }
}
