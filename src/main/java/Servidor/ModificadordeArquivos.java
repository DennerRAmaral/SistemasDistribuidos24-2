package Servidor;

import Base.Usuario;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;

public class ModificadordeArquivos {
    static void modifyFile(String filePath, ArrayList<Usuario> data) throws IOException {
        File fileToBeModified = new File(filePath);
        Gson gson = new Gson();
        FileWriter writer = null;
        writer = new FileWriter(String.valueOf(fileToBeModified));
        try {
            for (int i = 0; i < data.size(); i++) {

                writer.write(gson.toJson(data.get(i)) + "\n");
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
