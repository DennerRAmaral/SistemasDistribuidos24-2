package Servidor;

import Base.Usuario;

public class RetornaLocalizarUsuario {
    private int status;
    private String operacao;
    private Usuario usuario;

    public RetornaLocalizarUsuario(Usuario usuario) {
        this.status = 201;
        this.operacao = "localizarUsuario";
        this.usuario = usuario;
    }
}
