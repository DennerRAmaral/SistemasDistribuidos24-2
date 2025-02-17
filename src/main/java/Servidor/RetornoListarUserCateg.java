package Servidor;

public class RetornoListarUserCateg {
    private int status;
    private String operacao;
    private int[] categorias;

    public RetornoListarUserCateg(int[] categorias) {
        this.status = 201;
        this.operacao = "listarUsuarioCategorias";
        this.categorias = categorias;
    }
}
