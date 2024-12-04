package Cliente;

public class Cadastro {
    private final String operacao;
    private int ra;
    private String senha;
    private String nome;

    public Cadastro(int ra, String senha, String nome) {
        this.operacao = "cadastrarUsuario";
        this.ra = ra;
        this.setSenha(senha);
        this.nome = nome;
    }

    public int getRA() {
        return ra;
    }

    public void setRA(int ra) {
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
