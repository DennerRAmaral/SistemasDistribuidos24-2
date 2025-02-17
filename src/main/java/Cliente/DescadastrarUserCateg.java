package Cliente;

public class DescadastrarUserCateg {
    private String operacao;
    private String token;
    private String ra;
    private int categoria;

    public DescadastrarUserCateg(String token, String ra, int categoria) {
        this.setRa(ra);
        this.setOperacao("descadastrarUsuarioCategoria");
        this.setCategoria(categoria);
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
