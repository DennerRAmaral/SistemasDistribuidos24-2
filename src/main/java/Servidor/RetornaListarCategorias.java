package Servidor;

import Base.Categoria;

import java.util.ArrayList;

public class RetornaListarCategorias {
    private int status = 201;
    private String operacao;
    private ArrayList<Categoria> categorias;

    public RetornaListarCategorias(ArrayList<Categoria> categorias) {
        this.operacao = "listarCategorias";
        this.categorias = categorias;
    }
}