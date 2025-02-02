package Cliente;

import Base.Usuario;

public class EditarUsuario {
    private String token;
    private String operacao;
    private Usuario usuario;

    public EditarUsuario(Usuario usuario, String token) {
        this.token = token;
        this.operacao = "editarUsuario";
        this.usuario = usuario;
    }
}
