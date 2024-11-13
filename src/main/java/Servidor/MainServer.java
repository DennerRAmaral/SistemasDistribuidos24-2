package Servidor;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainServer extends Thread{
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
            System.out.println("Connection Socket Created");
            try {
                while (serverContinue) {
                    serverSocket.setSoTimeout(10000);
                    System.out.println("Waiting for Connection");
                    try {
                        new MainServer(serverSocket.accept());
                    } catch (SocketTimeoutException ste) {
                        System.out.println("Timeout Occurred");
                    }
                }
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 10008.");
            System.exit(1);
        } finally {
            try {
                System.out.println("Closing Server Connection Socket");
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close port: 10008.");
                System.exit(1);
            }
        }
        //serverSocket.close();
    }
    private MainServer(Socket clientSoc) {
        clientSocket = clientSoc;
        start();
    }
    public void run() {
        System.out.println("New Communication Thread Started");

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                    true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Server: " + inputLine);

                if (inputLine.equals("?"))
                    inputLine = new String("\"Bye.\" ends Client, " +
                            "\"End Server.\" ends Server");
                inputLine = inputLine.toUpperCase();
                out.println(inputLine);


                if (inputLine.equals("Bye."))
                    break;

                if (inputLine.equals("End Server."))
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

}
