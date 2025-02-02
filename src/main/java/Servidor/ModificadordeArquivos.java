package Servidor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModificadordeArquivos {
    static void modifyFile(String filePath, String oldString, String newString) throws IOException {
        File fileToBeModified = new File(filePath);
        String oldContent = "";
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(fileToBeModified));
        FileWriter writer = null;
        writer = new FileWriter(fileToBeModified);
        try {

            //Reading all the lines of input text file into oldContent
            String line = reader.readLine();
            while (line != null) {
                oldContent = oldContent + line + System.lineSeparator();

                line = reader.readLine();
            }
            //Replacing oldString with newString in the oldContent
            String newContent = oldContent.replaceAll(oldString, newString);
            //Rewriting the input text file with newContent

            writer.write(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
