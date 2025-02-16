package Servidor;

import Base.Aviso;

import java.util.ArrayList;

public class RetornaListarAvisos {
    private int status = 201;
    private String operacao;
    private ArrayList<Aviso> avisos;

    public RetornaListarAvisos(ArrayList<Aviso> avisos) {
        this.operacao = "listarAvisos";
        this.avisos = avisos;
    }

}
