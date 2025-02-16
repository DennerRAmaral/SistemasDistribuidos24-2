package Servidor;

import Base.Aviso;
import Base.Categoria;
import Base.Usuario;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;

public class ModificadordeArquivos {
    static void modifyuserFile(String filePath, ArrayList<Usuario> data) throws IOException {
        File fileToBeModified = new File(filePath);
        Gson gson = new Gson();
        FileWriter writer;
        writer = new FileWriter(String.valueOf(fileToBeModified));
        try {
            for (Usuario datum : data) {

                writer.write(gson.toJson(datum) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void modifycategFile(String filePath, ArrayList<Categoria> data) throws IOException {
        File fileToBeModified = new File(filePath);
        Gson gson = new Gson();
        FileWriter writer;
        writer = new FileWriter(String.valueOf(fileToBeModified));
        try {
            for (Categoria datum : data) {

                writer.write(gson.toJson(datum) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    static void modifyavisoFile(String filePath, ArrayList<Aviso> data) throws IOException {
        File fileToBeModified = new File(filePath);
        Gson gson = new Gson();
        FileWriter writer;
        writer = new FileWriter(String.valueOf(fileToBeModified));
        try {
            for (Aviso datum : data) {
                writer.write(gson.toJson(datum) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
