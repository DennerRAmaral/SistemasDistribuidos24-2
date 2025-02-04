package Servidor;

import Base.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileusers));
            for (String line : lines) {
                criarusuario(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            List<String> lines = Files.readAllLines(Paths.get(filecategorias));
            for (String line : lines) {
                if (!line.isBlank()) {
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

    public static String action(String json) throws IOException {
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if (!jsonObject.has("operacao")){
             return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
        }
        String operacao = jsonObject.get("operacao").getAsString();
        return switch (operacao) {
            case "login" -> login(json);
            case "cadastrarUsuario" -> cadastrarUsuario(json);
            case "listarUsuarios" -> listarusuarios(json, usuarios);
            case "localizarUsuario" -> localizarusuario(json, usuarios);
            case "editarUsuario" -> editarUsuario(json);
            case "logout" -> logout(json);
            default -> ("{\"status\": 401,\"mensagem\":  \"Operacao nao encontrada\"}");
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
                if (ra.equals(user.getRA())) {
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
            return "{\"status\": 401 ,\"operacao\": \"logout\",\"mensagem\":  \"Os campos recebidos não são válidos.\"}";
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

    public static String listarusuarios(String json, ArrayList<Usuario> usuarios) {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        if (logados.contains(token)) {
            if (token.equals(admin)) {
                Gson gson = new Gson();
                RetornaListarususarios lista = new RetornaListarususarios(usuarios);
                return gson.toJson(lista);
            } else {
                return "{\"status\": 401,\"operacao\": \"listarUsuarios\",\"mensagem\":  \"Credenciais incorretas.\"}";
            }
        } else {
            return "{\"status\": 401,\"operacao\": \"listarUsuarios\",\"mensagem\":  \"Credenciais incorretas.\"}";
        }
    }

    public static String localizarusuario(String json, ArrayList<Usuario> usuarios) {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        RetornaLocalizarUsuario retorno;
        Gson gson = new Gson();
        if (logados.contains(token)) {
            if (token.equals(admin)) {
                if (usuariodata.has("ra")) {
                    String ra = usuariodata.get("ra").getAsString();
                    for (Usuario usuario : usuarios) {
                        if (usuario.getRA().equals(ra)) {
                            retorno = new RetornaLocalizarUsuario(usuario);
                            return gson.toJson(retorno);
                        }
                    }
                    return "{\"status\": 401 ,\"operacao\": \"localizarusuario\",\"mensagem\":  \"Usuario nao encontrado.\"}";

                } else {
                    return "{\"status\": 401 ,\",\"mensagem\":  \"Os campos recebidos nao sao validos.\"}";
                }
            } else {
                if (token.equals(usuariodata.get("ra").getAsString()) && usuariodata.has("ra")) {
                    String ra = usuariodata.get("ra").getAsString();
                    for (Usuario usuario : usuarios) {
                        if (usuario.getRA().equals(ra)) {
                            retorno = new RetornaLocalizarUsuario(usuario);
                            return gson.toJson(retorno);
                        }
                    }
                } else {
                    return "{\"status\": 401,\"operacao\": \"localirUsuario\",\"mensagem\":  \"Acesso nao autorizado.\"}";
                }
            }
        } else {
            return "{\"status\": 401,\"operacao\": \"localizarUsuario\",\"mensagem\":  \"Credenciais incorretas.\"}";
        }
        return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
    }

    public static String editarUsuario(String json) throws IOException {
        Validador valido;
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        Gson gson = new Gson();
        Usuario userupdate;
        if (token.isBlank())
            return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
        if (logados.contains(token)) {
            if (admin.contains(token)||token.equals(usuariodata.get("ra").getAsString())) {
                JsonObject lista = usuariodata.getAsJsonObject("usuario");
                String userrecebido = lista.toString();
                valido = new Validador(userrecebido);
                if (valido.usuarioinvalido()) {
                    return "{ \"status\": 401,\"operacao\": \"editarUsuario\" , \"mensagem\":  \"Os campos recebidos não são válidos.}";
                } else {
                    String ra = lista.get("ra").getAsString();
                    String nome = lista.get("nome").getAsString();
                    String senha = lista.get("senha").getAsString();
                    for (int i = 0;i< usuarios.size();i++) {
                        if (usuarios.get(i).getRA().equals(ra)) {
                            String olduser = gson.toJson(usuarios.get(i));
                            userupdate = new Usuario(ra,nome,senha);
                            String newuser = gson.toJson(userupdate);
                            usuarios.set(i,userupdate);
                            ModificadordeArquivos.modifyFile(fileusers,usuarios);
                            return "{ \"status\": 201,\"operacao\": \"editarUsuario\", \"mensagem\":  \"Edição realizada com sucesso.\"}";
                        }
                    }
                    return "{\"status\": 401 ,\"operacao\": \"editarUsuarios\",\"mensagem\":  \"Usuario nao encontrado.\"}";
                }
            }else {
                return "{\"status\": 401,\"operacao\": \"editarUsuario\",\"mensagem\":  \"Acesso nao autorizado.\"}";
            }
        }
        return "{ \"status\": 401,\"operacao\": \"editarUsuario\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
    }

}



