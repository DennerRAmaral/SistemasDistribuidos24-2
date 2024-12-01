package Servidor;

import Base.Usuario;
import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer extends Thread {
    protected Socket clientSocket;
    public static ArrayList<Usuario> usuarios;
    public static ArrayList<String> logados;

    public static void main(String[] args) throws IOException {
        usuarios = new ArrayList<Usuario>();
        logados = new ArrayList<String>();
        usuarios.add(new Usuario("1234567", "Admin", "abretesesamo"));
        ServerSocket serverSocket = null;
        int serverport;
        Scanner scan = new Scanner(System.in);
        InetAddress localHost = InetAddress.getLocalHost();
        System.out.println("Ip do servidor: " + localHost);
        System.out.println("Insira a porta do server:");
        serverport = scan.nextInt();
        try {
            serverSocket = new ServerSocket(serverport);
            System.out.println("Soquete de conexao ok");
            try {
                while (true) {
                    serverSocket.setSoTimeout(10000);
                    System.out.println("Aguardando...");
                    try {
                        new MainServer(serverSocket.accept());
                    } catch (SocketTimeoutException ste) {
                        System.out.println("Timeout");
                    }
                }
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Não pode ouvir a porta:" + serverport);
            System.exit(1);
        } finally {
            try {
                System.out.println("Fechando soquete");
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close port: 10008.");
                System.exit(1);
            }
        }
    }

    private MainServer(Socket clientSoc) {
        clientSocket = clientSoc;
        start();
    }

    public void run() {
        System.out.println("Nova Thread iniciada");

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                    true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            String retorno;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recebido pelo server: " + inputLine);
                retorno = action(inputLine);
                System.out.println("Enviando ao cliente: " + retorno);
                out.println(retorno);
            }

            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Problem with Communication Server");
            System.exit(1);
        }
    }

    public static String action(String json) {
        System.out.println(json);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject(); // PARSER
        String operacao = jsonObject.get("operacao").getAsString();
        return switch (operacao) {
            case "login" -> login(json);
            default ->
                    ("{\"status\": 401,\"operacao\": \"operacao de entrada do cliente\",\"mensagem\":  \"Operacao nao encontrada\"}");
        };


    }

    public static String login(String json) {
        Validador valido = new Validador(json);
        JsonObject logindata = JsonParser.parseString(json).getAsJsonObject();
        if (valido.temcamponulo() || valido.loginincorreto()) {
            return "{\"status\": 401,\"operacao\": \"login\",\"mensagem\":  \"Os campos recebidos nao sao validos.\"}";
        } else {
            for (Usuario user : usuarios) {
                if ((logindata.get("ra").getAsString()).equals(user.getRA())) {
                    if ((logindata.get("senha").getAsString()).equals(user.getSenha())) {
                        logados.add(logindata.get("ra").getAsString());
                        return "{\"operacao\": \"login\",\"status\": 200 , \"token\":  \"" + logindata.get("ra").getAsString() + "\"}";
                    }else {
                        return "{\"status\": 401,\"operacao\": \"login\",\"mensagem\":  \"Credenciais incorretas.\"}";
                    }
                }
            }
            return "{\"status\": 401,\"operacao\": \"login\",\"mensagem\":  \"Credenciais incorretas.\"}";
        }
    }

}
