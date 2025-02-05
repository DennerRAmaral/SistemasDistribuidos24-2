package Servidor;

import Base.Usuario;

import java.util.ArrayList;

public class RetornaListarususarios {
    private int status = 201;
    private String operacao;
    private ArrayList<Usuario> usuarios;

    public RetornaListarususarios(ArrayList<Usuario> usuario) {
        this.operacao = "listarUsuarios";
        this.usuarios = usuario;
    }
}
