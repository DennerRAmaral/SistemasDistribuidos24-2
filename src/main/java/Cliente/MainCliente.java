package Cliente;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainCliente {
    static String token = "";
    public static void main(String[] args) throws IOException {
        //Variavveis locais
        Gson gson = new Gson();
        String serverHostname;
        int serverport;
        String userinput;
        Socket soquete = null;
        PrintWriter out = null;
        BufferedReader in = null;
        boolean continuar = false;
        Scanner scan = new Scanner(System.in);


        //Insercao de server
        System.out.println("Insira o ip do server:");
        serverHostname = scan.nextLine();
        System.out.println("Insira a porta do server:");
        serverport = scan.nextInt();
        scan.nextLine();

        //Conexao ao sistema
        if (args.length > 0)
            serverHostname = args[0];
        System.out.println("Conectando a" +
                serverHostname + " na porta " + serverport);
        try {
            soquete = new Socket(serverHostname, serverport);
            out = new PrintWriter(soquete.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    soquete.getInputStream()));
            continuar = true;

        } catch (UnknownHostException e) {
            System.err.println("Host desconhecido: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: " + serverHostname);
            System.exit(1);
        }


        while (continuar) {
            //System.out.println("Usuario padrao ou administrador?\n  1 - Padrao\n  2 - Administrador  \n  0 - sair");
            System.out.println("====\nQUAL ACAO DESEJA?\n====\n  1 - Login\n  2 - Cadastro\n  9 - logout\n  0 - fechar");
            userinput = scan.nextLine();
            switch (userinput) {
                case "1":
                    MainCliente.efetuarlogin(scan, gson, out, in);
                    break;
                case "2":
                    System.out.println("s");
                    break;
                case "0":
                    System.out.println("fechando aplicacao");
                    continuar = false;
                    break;
                default:
                    System.out.println("Opcao invalida\n");
            }

        }



        /*
        System.out.print("input: ");
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            inputLine = in.readLine();
            System.out.println("echo: " + inputLine);
            if (inputLine.equals("BYE")) {
                break;
            }
            System.out.print("input: ");
        }*/

        out.close();
        in.close();
        soquete.close();
    }

    protected static void efetuarlogin(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        System.out.println("====\nInsira seu RA:");
        String ra = scan.nextLine();
        String retorno;
        System.out.println("Insira sua senha:");
        String senha = scan.nextLine();
        Login login = new Login(ra, senha);
        String json = gson.toJson(login);
        System.out.println("Enviando ao server:"+json);
        out.println(json);
        retorno = in.readLine();
        System.out.println("Recebido do Server: "+retorno);
        JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
        if (retornojson.get("status").getAsInt()==200){
            System.out.println("Login efetuado");
            token = retornojson.get("token").getAsString();
        }else {
            System.out.println(retornojson.get("mensagem").getAsString());

        }
    }
}
