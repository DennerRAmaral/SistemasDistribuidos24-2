package Cliente;

public class Cadastro {
    private final String operacao;
    private String ra;
    private String senha;
    private String nome;

    public Cadastro(String ra, String senha, String nome) {
        this.operacao = "cadastrarUsuario";
        this.ra = ra;
        this.setSenha(senha);
        this.nome = nome;
    }

    public String getRA() {
        return ra;
    }

    public void setRA(String ra) {
        this.ra = ra;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getOperacao() {
        return operacao;
    }
}
