package Servidor;

import Base.Aviso;

public class RetornaLocalizarAviso {
    private int status;
    private String operacao;
    private Aviso aviso;

    public RetornaLocalizarAviso(Aviso aviso) {
        this.status = 201;
        this.operacao = "localizarAviso";
        this.aviso = aviso;
    }
}
