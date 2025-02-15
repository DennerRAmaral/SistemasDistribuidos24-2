package Cliente;

import Base.Aviso;

public class SalvarAviso {
    private String token;
    private String operacao;
    private Aviso aviso;

    public SalvarAviso(String token, Aviso aviso) {
        this.token = token;
        this.operacao = "salvarAviso";
        this.aviso = aviso;
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

    public Aviso getAviso() {
        return aviso;
    }

    public void setAviso(Aviso aviso) {
        this.aviso = aviso;
    }
}
