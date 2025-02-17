package Servidor;

import Base.Aviso;

import java.util.ArrayList;

public class RetornaListarAvisosUsuario {
    private int status = 201;
    private String operacao;
    private ArrayList<Aviso> avisos;

    public RetornaListarAvisosUsuario(ArrayList<Aviso> avisos) {
        this.operacao = "listarUsuarioAvisos";
        this.avisos = avisos;
    }

}
