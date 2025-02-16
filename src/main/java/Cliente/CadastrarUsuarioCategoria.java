package Cliente;

public class CadastrarUsuarioCategoria {
    private String token;
    private String operacao;
    private String ra;
    private int categoria;

    public CadastrarUsuarioCategoria(String token, String ra, int categoria) {
        this.setToken(token);
        this.setOperacao("cadastrarUsuarioCategoria");
        this.setRa(ra);
        this.setCategoria(categoria);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }
}
