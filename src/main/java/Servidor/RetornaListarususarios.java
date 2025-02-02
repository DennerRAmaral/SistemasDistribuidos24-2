package Servidor;

import Base.Usuario;

import java.util.ArrayList;

public class RetornaListarususarios {
    private int status = 201;
    private String opercao;
    private ArrayList<Usuario> usuario;

    public RetornaListarususarios(ArrayList<Usuario> usuario) {
        this.opercao = "listarUsuarios";
        this.usuario = usuario;
    }
}
