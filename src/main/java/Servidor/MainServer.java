package Servidor;

import Base.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainServer extends Thread {
    protected Socket clientSocket;
    public static ArrayList<Usuario> usuarios;
    public static ArrayList<String> logados;
    public static String fileusers = "usuarios.txt";
    public static String filecategorias = "categorias.txt";
    public static String admin = "1234567";

    public static void main(String[] args) throws IOException {
        usuarios = new ArrayList<>();
        logados = new ArrayList<>();
        Gson geson = new Gson();

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileusers));
            for (String line : lines) {
                criarusuario(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }try {
            List<String> lines = Files.readAllLines(Paths.get(filecategorias));
            for (String line : lines) {
                if (!line.isBlank()){
                    criarusuario(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ServerSocket serverSocket = null;
        int serverport = 25000;
        InetAddress localHost = InetAddress.getLocalHost();
        System.out.println("Ip do servidor: " + localHost + "\nPorta de conexao: " + serverport);
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
                assert serverSocket != null;
                Objects.requireNonNull(serverSocket).close();
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
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

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
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject(); // PARSER
        String operacao = jsonObject.get("operacao").getAsString();
        return switch (operacao) {
            case "login" -> login(json);
            case "cadastrarUsuario" -> cadastrarUsuario(json);

            case "logout" -> logout(json);
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
                        if (logados.contains(logindata.get("ra").getAsString())) {
                            System.out.println("Usuario ja logado");
                            return "{\"status\": 401,\"operacao\": \"login\",\"mensagem\":  \"Usuario ja logado\"}";
                        } else {
                            logados.add(String.valueOf(logindata.get("ra").getAsInt()));
                            return "{\"operacao\": \"login\",\"status\": 200 , \"token\":  \"" + logindata.get("ra").getAsString() + "\"}";
                        }
                    } else {
                        return "{\"status\": 401,\"operacao\": \"login\",\"mensagem\":  \"Credenciais incorretas.\"}";
                    }
                }
            }
            return "{\"status\": 401,\"operacao\": \"login\",\"mensagem\":  \"Credenciais incorretas.\"}";
        }
    }

    public static String cadastrarUsuario(String json) {
        Validador valido = new Validador(json);
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        if (valido.usuarioinvalido()) {
            return "{\"status\": 401 ,\"operacao\": \"cadastrarUsuario\",\"mensagem\":  \"Os campos recebidos sao invalidos\"}";
        } else {
            String ra = usuariodata.get("ra").getAsString();
            for (Usuario user : usuarios) {
                if (ra.equals(user.getRA()) ) {
                    return "{\"status\": 401 ,\"operacao\": \"cadastrarUsuario\",\"mensagem\":  \"Não foi cadastrar pois o usuario informado ja existe\"}";
                }
            }
            criarusuario(json);
            return "{ \"status\": 201, \"operacao\": \"cadastrarUsuario\",\"mensagem\":  \"Cadastro realizado com sucesso.\"}";
        }
    }

    public static String logout(String json) {
        Validador valido = new Validador(json);
        JsonObject logoutdata = JsonParser.parseString(json).getAsJsonObject();
        if (valido.logoutincorreto()) {
            return "{\"status\": 401 ,\"operacao\": \"logout\",\"mensagem\":  \"Não foi possível ler o json recebido.\"}";
        } else {
            String token = logoutdata.get("token").getAsString();
            if (logados.contains(token)) {
                boolean sucessologout = logados.remove(token);
                if (sucessologout) {
                    return "{\"operacao\": \"logout\", \"status\": 200 }";
                }
            }
            return "{\"status\": 401 ,\"operacao\": \"logout\",\"mensagem\":  \"Erro ao realizar logout.\"}";
        }
    }

    public static void criarusuario(String json) {
        Validador valido = new Validador(json);
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        if (valido.usuarioinvalido()) {
            System.out.println("Insercao de usuario invalida");
        } else {
            String ra = usuariodata.get("ra").getAsString();
            String nome = usuariodata.get("nome").getAsString();
            String senha = usuariodata.get("senha").getAsString();
            usuarios.add(new Usuario(ra, nome, senha));
        }
    }

    public static String listarusuarios(String json){
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        if (logados.contains(token)){
            if (token.equals(admin)){

            }else {
                return "{\"status\": 401,\"operacao\": \"listarUsuarios\",\"mensagem\":  \"Credenciais incorretas.\"}";
            }
        }else {
            return "{\"status\": 401,\"operacao\": \"listarUsuarios\",\"mensagem\":  \"Credenciais incorretas.\"}";
        }
    }
}
