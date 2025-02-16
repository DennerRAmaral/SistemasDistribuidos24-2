package Base;

import java.util.Arrays;

public class UserCateg {
    private String ra;
    private int[] categorias;

    public UserCateg(String ra, int[] categoria) {
        this.setRa(ra);
        this.setCategorias(categoria);
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public int[] getCategorias() {
        return categorias;
    }

    public void setCategorias(int[] categorias) {
        this.categorias = categorias;
    }

    /*public String mostrarCategorias() {
        String categ = "[";
        for (int i : categorias) {
            categ = categ.concat(i + ",");
        }
        categ = categ.substring(0, categ.lastIndexOf(','));
        categ = categ.concat("]");
        return categ;
    }*/

    public void addCategoria(int i) {
        boolean alreadyhas = false;
        for (int categoria : categorias) {
            if (categoria == i) {
                alreadyhas = true;
                break;
            }
        }
        if (!alreadyhas){
            int[] newArray;
            newArray = new int[categorias.length + 1];
            System.arraycopy(categorias, 0, newArray, 0, categorias.length);
            newArray[categorias.length] = i;
            categorias = newArray;
        }

    }

    public String toString() {
        return "Categorias do usuario||  RA: " + getRa() + "|| Categorias: " + Arrays.toString(categorias) + "||";
    }
}
