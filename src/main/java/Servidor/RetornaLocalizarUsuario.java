package Servidor;

import Base.Usuario;

public class RetornaLocalizarUsuario {
    private int status;
    private String opercao;
    private Usuario usuario;

    public RetornaLocalizarUsuario(Usuario usuario) {
        this.status = 201;
        this.opercao = "localizarUsuario";
        this.usuario = usuario;
    }
}
