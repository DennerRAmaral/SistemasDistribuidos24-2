package Base;

public class Aviso {
    private int id;
    private int categoria;
    private String titulo;
    private String descricao;

    public Aviso(int id,int categoria, String titulo, String descricao){
        this.id = id;
        this.categoria = categoria;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }
    public String toString() {
        return "Aviso ||" + "id: " + id + "|| categoria: " + categoria + "||\n TITULO: "+ titulo+"\n DESCRICAO: \n"+descricao+"\n---------------";
    }
}
