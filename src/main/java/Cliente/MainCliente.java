package Cliente;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;
import java.util.*;

public class MainCliente {
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        String serverHostname;
        int serverport;
        Scanner scan = new Scanner(System.in);
        System.out.println("Insira o ip do server:");
        serverHostname = scan.nextLine();
        System.out.println("Insira a porta do server:");
        serverport = scan.nextInt();


        if (args.length > 0)
            serverHostname = args[0];
        System.out.println("Attemping to connect to host " +
                serverHostname + " on port" + serverport);

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(serverHostname, serverport);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to: " + serverHostname);
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        String userInput;
        String inputLine;
        System.out.print("input: ");
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            inputLine = in.readLine();
            System.out.println("echo: " + inputLine);
            if (inputLine.equals("BYE")) {
                break;
            }
            System.out.print("input: ");
        }

        out.close();
        in.close();
        //stdIn.close();
        echoSocket.close();
    }
}