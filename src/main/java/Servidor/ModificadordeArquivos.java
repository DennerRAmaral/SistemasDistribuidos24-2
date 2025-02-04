package Servidor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class ModificadordeArquivos {
    static void modifyFile(String filePath, String oldString, String newString) throws IOException {
        File fileToBeModified = new File(filePath);
        StringBuilder oldContent = new StringBuilder();
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(fileToBeModified));
        FileWriter writer = null;
        writer = new FileWriter(fileToBeModified);
        try {
            //Reading all the lines of input text file into oldContent
            String line ;
            line = reader.readLine();
            while (line != null) {
                oldContent.append(line).append("\n");
                line = reader.readLine();
            }
            System.out.println(oldContent);
            //Replacing oldString with newString in the oldContent
            String newContent = oldContent.toString().replaceAll(Pattern.quote(oldString), Pattern.quote(newString));
            //Rewriting the input text file with newContent
            System.out.println(newContent);
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
