package Cliente;

public class ListarUserAvisos {
    private String operacao;
    private String token;
    private String ra;

    public ListarUserAvisos(String token, String ra) {
        this.operacao = "listarUsuarioAvisos";
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
