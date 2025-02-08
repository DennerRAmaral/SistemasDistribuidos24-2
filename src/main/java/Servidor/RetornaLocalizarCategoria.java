package Servidor;

import Base.Categoria;

public class RetornaLocalizarCategoria {
    private int status;
    private String operacao;
    private Categoria categoria;

    public RetornaLocalizarCategoria(Categoria categoria) {
        this.status = 201;
        this.operacao = "localizarCategoria";
        this.categoria = categoria;
    }
}
