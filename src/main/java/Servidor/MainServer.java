package Servidor;

import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer extends Thread {
    protected static boolean serverContinue = true;
    protected Socket clientSocket;

    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
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
                while (serverContinue) {
                    serverSocket.setSoTimeout(10000);
                    System.out.println("Aguardando...");
                    try {
                        new MainServer(serverSocket.accept(), gson);
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
        serverSocket.close();
    }

    private MainServer(Socket clientSoc, Gson gson) {
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

            while ((inputLine = in.readLine()) != null) {
                action(inputLine);
                serverContinue = false;
            }

            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Problem with Communication Server");
            System.exit(1);
        }
    }
    public String action(String json){
        Gson geson = new Gson();
        System.out.println(json);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject(); // PARSER
        String operacao = jsonObject.get("operacao").getAsString();
        switch (operacao){
            case "login": return login(json);
            default: return ("{\"status\": 401,\"operacao\": \"operacao de entrada do cliente\",\"mensagem\":  \"Operacao nao encontrada\"}");

        }


    }

    public String login(String json){
        return "sa";
    }

}
