package Cliente;


import Base.Categoria;

public class SalvarCategoria {
    private String token;
    private String operacao;
    private Categoria categoria;

    public SalvarCategoria(String token,Categoria categoria) {
        this.token = token;
        this.operacao = "salvarCategoria";
        this.categoria = categoria;
    }
}
