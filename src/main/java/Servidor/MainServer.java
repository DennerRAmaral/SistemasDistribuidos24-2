package Servidor;

import Base.Aviso;
import Base.Categoria;
import Base.UserCateg;
import Base.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
    public static ArrayList<Categoria> categorias;
    public static ArrayList<Aviso> avisos;
    public static ArrayList<String> logados;
    public static ArrayList<UserCateg> userCategs;
    public static String fileusers = "usuarios.txt";
    public static String filecategorias = "categorias.txt";
    public static String fileavisos = "avisos.txt";
    public static String fileusercategs = "usercateg.txt";
    public static String admin = "1234567";

    public static void main() throws IOException {
        usuarios = new ArrayList<>();
        categorias = new ArrayList<>();
        avisos = new ArrayList<>();
        userCategs = new ArrayList<>();
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
                    criarcategoria(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileavisos));
            for (String line : lines) {
                criaraviso(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileusercategs));
            for (String line : lines) {
                criarusuercateg(line);
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
                do {
                    serverSocket.setSoTimeout(1000000);
                    System.out.println("Aguardando...");
                    try {
                        new MainServer(serverSocket.accept());
                    } catch (SocketTimeoutException ste) {
                        System.out.println("Timeout");
                    }
                } while (!Thread.currentThread().isInterrupted());
                Objects.requireNonNull(serverSocket).close();
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
                System.out.println("Usuarios logados:");
                for (Usuario user : usuarios) {
                    if (logados.contains(user.getRA())) {
                        System.out.println(user.getNome());
                    }
                }
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
        if (!jsonObject.has("operacao")) {
            return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
        }
        String operacao = jsonObject.get("operacao").getAsString();
        return switch (operacao) {
            case "login" -> login(json);
            case "cadastrarUsuario" -> cadastrarUsuario(json);
            case "listarUsuarios" -> listarusuarios(json, usuarios);
            case "localizarUsuario" -> localizarusuario(json, usuarios);
            case "editarUsuario" -> editarUsuario(json);
            case "excluirUsuario" -> excluirUsuario(json);
            case "salvarCategoria" -> salvarCategoria(json);
            case "listarCategorias" -> listarcategorias(json, categorias);
            case "localizarCategoria" -> localizarcategoria(json);
            case "excluirCategoria" -> excluirCategoria(json);
            case "salvarAviso" -> salvarAviso(json);
            case "listarAvisos" -> listarAvisos(json);
            case "localizarAviso" -> localizaraviso(json);
            case "excluirAviso" -> excluirAviso(json);
            case "cadastrarUsuarioCategoria" -> cadastrarUsuarioCategoria(json);
            case "listarUsuarioCategorias" -> listarUserCateg(json);
            case "descadastrarUsuarioCategoria" -> excluirUsuarioCategoria(json);
            case "listarUsuarioAvisos" -> listarAvisosUser(json);
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
            try {
                ModificadordeArquivos.modifyuserFile(fileusers, usuarios);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    public static void criarcategoria(String json) throws IOException {
        Validador valido = new Validador(json);
        JsonObject categoriaodata = JsonParser.parseString(json).getAsJsonObject();
        if (valido.categoriainvalida()) {
            System.out.println("Insercao de categoria invalida");
        } else {
            int id = 0;
            for (int i = 0; i < categorias.size(); i++) {
                if (i != categorias.get(i).getId()) {
                    id = i;
                    break;
                }
            }
            if (id == 0) {
                id = categorias.size();
            }
            String nome = categoriaodata.get("nome").getAsString();
            categorias.add(id, new Categoria(id, nome));
            try {
                ModificadordeArquivos.modifycategFile(filecategorias, categorias);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void criaraviso(String json) throws IOException {
        Validador valido = new Validador(json);
        JsonObject avisodata = JsonParser.parseString(json).getAsJsonObject();
        if (valido.avisoinvalido()) {
            System.out.println("Insercao de aviso invalida");
        } else {
            int id = 0;
            for (int i = 0; i < avisos.size(); i++) {
                if (i != avisos.get(i).getId()) {
                    id = i;
                    break;
                }
            }
            if (id == 0) {
                id = avisos.size();
            }
            int categoria = avisodata.get("categoria").getAsInt();
            String titulo = avisodata.get("titulo").getAsString();
            String descricao = avisodata.get("descricao").getAsString();
            avisos.add(id, new Aviso(id, categoria, titulo, descricao));
            try {
                ModificadordeArquivos.modifyavisoFile(fileavisos, avisos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void criarusuercateg(String json) {
        Gson gson = new Gson();
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String ra = usuariodata.get("ra").getAsString();
        JsonArray categoriasarary = usuariodata.get("categorias").getAsJsonArray();
        int[] categorias = gson.fromJson(categoriasarary, int[].class);
        userCategs.add(new UserCateg(ra, categorias));
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
        Usuario userupdate;
        JsonObject lista = usuariodata.getAsJsonObject("usuario");
        if (token.isBlank())
            return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
        if (logados.contains(token)) {
            if (admin.contains(token) || lista.get("ra").getAsString().equals(token)) {
                String userrecebido = lista.toString();
                valido = new Validador(userrecebido);
                if (valido.usuarioinvalido()) {
                    return "{ \"status\": 401,\"operacao\": \"editarUsuario\" , \"mensagem\":  \"Os campos recebidos não são válidos.}";
                } else {
                    String ra = lista.get("ra").getAsString();
                    String nome = lista.get("nome").getAsString();
                    String senha = lista.get("senha").getAsString();
                    for (int i = 0; i < usuarios.size(); i++) {
                        if (usuarios.get(i).getRA().equals(ra)) {
                            userupdate = new Usuario(ra, nome, senha);
                            usuarios.set(i, userupdate);
                            ModificadordeArquivos.modifyuserFile(fileusers, usuarios);
                            return "{ \"status\": 201,\"operacao\": \"editarUsuario\", \"mensagem\":  \"Edição realizada com sucesso.\"}";
                        }
                    }
                    return "{\"status\": 401 ,\"operacao\": \"editarUsuarios\",\"mensagem\":  \"Usuario nao encontrado.\"}";
                }
            } else {
                return "{\"status\": 401,\"operacao\": \"editarUsuario\",\"mensagem\":  \"Acesso nao autorizado.\"}";
            }
        }
        return "{ \"status\": 401,\"operacao\": \"editarUsuario\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
    }

    public static String excluirUsuario(String json) throws IOException {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        if (token.isBlank())
            return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
        if (logados.contains(token)) {
            if (admin.contains(token) || token.equals(usuariodata.get("ra").getAsString())) {
                String ra = usuariodata.get("ra").getAsString();
                for (int i = 0; i < usuarios.size(); i++) {
                    if (usuarios.get(i).getRA().equals(ra)) {
                        usuarios.remove(i);
                        ModificadordeArquivos.modifyuserFile(fileusers, usuarios);
                        logados.remove(ra);
                        return "{ \"status\": 201,\"operacao\": \"excluirUsuario\", \"mensagem\":  \"Exclusao realizada com sucesso.\"}";
                    }
                }
                return "{\"status\": 401 ,\"operacao\": \"excluirUsuario\",\"mensagem\":  \"Usuario nao encontrado.\"}";
            }
        } else {
            return "{\"status\": 401,\"operacao\": \"excluirUsuario\",\"mensagem\":  \"Acesso nao autorizado.\"}";
        }
        return "{ \"status\": 401,\"operacao\": \"excluirUsuario\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
    }

    public static String salvarCategoria(String json) throws IOException {
        Validador valido;
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        if (token.isBlank())
            return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
        if (logados.contains(token)) {
            if (admin.contains(token)) {
                JsonObject lista = usuariodata.getAsJsonObject("categoria");
                String categoriarecebida = lista.toString();
                valido = new Validador(categoriarecebida);
                if (valido.categoriainvalida()) {
                    return "{ \"status\": 401,\"operacao\": \"editarusuario\" , \"mensagem\":  \"Os campos recebidos não são válidos.}";
                } else {
                    int id = lista.get("id").getAsInt();
                    String nome = lista.get("nome").getAsString();
                    if (id == 0) {
                        criarcategoria(categoriarecebida);
                        ModificadordeArquivos.modifycategFile(filecategorias, categorias);
                        return "{ \"status\": 201,\"operacao\": \"salvarCategoria\", \"mensagem\":  \"Edição realizada com sucesso.\"}";
                    } else {
                        for (int i = 0; i < categorias.size(); i++) {
                            if (categorias.get(i).getId() == id) {
                                Categoria newcateg = new Categoria(id, nome);
                                categorias.set(i, newcateg);
                                ModificadordeArquivos.modifycategFile(filecategorias, categorias);
                                return "{ \"status\": 201,\"operacao\": \"salvarCategoria\", \"mensagem\":  \"Edição realizada com sucesso.\"}";
                            }
                        }
                        return "{\"status\": 401 ,\"operacao\": \"salvarCategoria\",\"mensagem\":  \"Categoria nao encontrado.\"}";
                    }
                }
            } else {
                return "{\"status\": 401,\"operacao\": \"salvarCategoria\",\"mensagem\":  \"Acesso nao autorizado.\"}";
            }
        }
        return "{ \"status\": 401,\"operacao\": \"salvarCategoria\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
    }

    public static String listarcategorias(String json, ArrayList<Categoria> categorias) {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        if (logados.contains(token)) {
            if (token.equals(admin)) {
                Gson gson = new Gson();
                RetornaListarCategorias lista = new RetornaListarCategorias(categorias);
                return gson.toJson(lista);
            } else {
                return "{\"status\": 401,\"operacao\": \"listarCategorias\",\"mensagem\":  \"Credenciais incorretas.\"}";
            }
        } else {
            return "{\"status\": 401,\"operacao\": \"listarCategorias\",\"mensagem\":  \"Credenciais incorretas.\"}";
        }
    }

    public static String localizarcategoria(String json) {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        RetornaLocalizarCategoria retorno;
        Gson gson = new Gson();
        if (logados.contains(token)) {
            if (token.equals(admin)) {
                if (usuariodata.has("id")) {
                    int id = usuariodata.get("id").getAsInt();
                    for (Categoria categoria : categorias) {
                        if (categoria.getId() == id) {
                            retorno = new RetornaLocalizarCategoria(categoria);
                            return gson.toJson(retorno);
                        }
                    }
                    return "{\"status\": 401 ,\"operacao\": \"localizarCategoria\",\"mensagem\":  \"Categoria nao encontrada.\"}";

                } else {
                    return "{\"status\": 401 ,\",\"mensagem\":  \"Os campos recebidos nao sao validos.\"}";
                }
            } else {
                return "{\"status\": 401,\"operacao\": \"localizarCategoria\",\"mensagem\":  \"Acesso nao autorizado.\"}";
            }

        } else {
            return "{\"status\": 401,\"operacao\": \"localizarCategoria\",\"mensagem\":  \"Credenciais incorretas.\"}";
        }
    }

    public static String excluirCategoria(String json) throws IOException {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        if (token.isBlank())
            return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
        if (logados.contains(token)) {
            if (admin.contains(token)) {
                int id = usuariodata.get("id").getAsInt();
                for (int i = 0; i < categorias.size(); i++) {
                    if (categorias.get(i).getId() == id) {
                        categorias.remove(i);
                        ModificadordeArquivos.modifycategFile(filecategorias, categorias);
                        return "{ \"status\": 201,\"operacao\": \"excluirCategoria\", \"mensagem\":  \"Exclusao realizada com sucesso.\"}";
                    }
                }
                return "{\"status\": 401 ,\"operacao\": \"excluirCategoria\",\"mensagem\":  \"Categoria nao encontrada.\"}";
            }
        } else {
            return "{\"status\": 401,\"operacao\": \"excluirCategoria\",\"mensagem\":  \"Acesso nao autorizado.\"}";
        }
        return "{ \"status\": 401,\"operacao\": \"excluirCategoria\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
    }

    public static String salvarAviso(String json) throws IOException {
        Validador valido;
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        if (token.isBlank())
            return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
        if (logados.contains(token)) {
            if (admin.contains(token)) {
                JsonObject lista = usuariodata.getAsJsonObject("aviso");
                String avisorecebido = lista.toString();
                valido = new Validador(avisorecebido);
                if (valido.avisoinvalido()) {
                    return "{ \"status\": 401,\"operacao\": \"salvarAviso\" , \"mensagem\":  \"Os campos recebidos não são válidos.}";
                } else {
                    int id = lista.get("id").getAsInt();
                    int categoria = lista.get("categoria").getAsInt();
                    String titulo = lista.get("titulo").getAsString();
                    String descricao = lista.get("descricao").getAsString();
                    boolean temcateg = false;
                    for (Categoria value : categorias) {
                        if (categoria == value.getId()) {
                            temcateg = true;
                            break;
                        }
                    }
                    if (!temcateg) {
                        return "{\"status\":401,\"operacao\":\"salvarAviso\",\"mensagem\":\"Categoria não encontrada.\"}";
                    }
                    if (id == 0) {
                        criaraviso(avisorecebido);
                        ModificadordeArquivos.modifyavisoFile(fileavisos, avisos);
                        return "{ \"status\": 201,\"operacao\": \"salvarAviso\", \"mensagem\":  \"Edição realizada com sucesso.\"}";
                    } else {
                        for (int i = 0; i < avisos.size(); i++) {
                            if (avisos.get(i).getId() == id) {
                                Aviso newaviso = new Aviso(id, categoria, titulo, descricao);
                                avisos.set(i, newaviso);
                                ModificadordeArquivos.modifyavisoFile(fileavisos, avisos);
                                return "{ \"status\": 201,\"operacao\": \"salvarAviso\", \"mensagem\":  \"Edição realizada com sucesso.\"}";
                            }
                        }
                        return "{\"status\": 401 ,\"operacao\": \"salvarAviso\",\"mensagem\":  \"Aviso nao encontrado.\"}";
                    }
                }
            } else {
                return "{\"status\": 401,\"operacao\": \"salvarAviso\",\"mensagem\":  \"Acesso nao autorizado.\"}";
            }
        }
        return "{ \"status\": 401,\"operacao\": \"salvarAviso\"mensagem\":\"Não foi possível possível processar a requisição.\"}";

    }

    public static String listarAvisos(String json) {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        int categoria = usuariodata.get("categoria").getAsInt();
        if (logados.contains(token)) {
            Gson gson = new Gson();
            if (categoria == 0) {
                RetornaListarAvisos lista = new RetornaListarAvisos(avisos);
                return gson.toJson(lista);
            } else {
                ArrayList<Aviso> avisosfiltrados = new ArrayList<>();
                for (Aviso aviso : avisos) {
                    if (aviso.getCategoria() == categoria) {
                        avisosfiltrados.add(aviso);
                    }
                }
                if (avisosfiltrados.isEmpty()) {
                    return "{\"status\": 401 ,\"operacao\": \"listarAvisos\" ,\"mensagem\":  \"Categoria não encontrada;\"}";
                } else {
                    RetornaListarAvisos lista = new RetornaListarAvisos(avisosfiltrados);
                    return gson.toJson(lista);
                }
            }
        } else {
            return "{\"status\": 401,\"operacao\": \"listarAvisos\",\"mensagem\":  \"Credenciais incorretas\"}";
        }
    }

    public static String localizaraviso(String json) {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        RetornaLocalizarAviso retorno;
        Gson gson = new Gson();
        if (logados.contains(token)) {
            if (usuariodata.has("id")) {
                int id = usuariodata.get("id").getAsInt();
                for (Aviso aviso : avisos) {
                    if (aviso.getId() == id) {
                        retorno = new RetornaLocalizarAviso(aviso);
                        return gson.toJson(retorno);
                    }
                }
                return "{\"status\": 401 ,\"operacao\": \"localizarAviso\",\"mensagem\":  \"Aviso nao encontrada.\"}";
            } else {
                return "{\"status\": 401 ,\",\"mensagem\":  \"Os campos recebidos nao sao validos.\"}";
            }
        } else {
            return "{\"status\": 401,\"operacao\": \"localizarAviso\",\"mensagem\":  \"Credenciais incorretas.\"}";
        }
    }

    public static String excluirAviso(String json) throws IOException {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        if (token.isBlank())
            return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
        if (logados.contains(token)) {
            if (admin.contains(token)) {
                int id = usuariodata.get("id").getAsInt();
                for (int i = 0; avisos.size() > i; i++) {
                    if (avisos.get(i).getId() == id) {
                        avisos.remove(i);
                        ModificadordeArquivos.modifyavisoFile(fileavisos, avisos);
                        return "{ \"status\": 201,\"operacao\": \"excluirAviso\", \"mensagem\":  \"Exclusao realizada com sucesso\"}";
                    }
                }
                return "{\"status\": 401 ,\"operacao\": \"excluirAviso\",\"mensagem\":  \"Aviso nao encontrada.\"}";
            }
        } else {
            return "{\"status\": 401,\"operacao\": \"excluirAviso\",\"mensagem\":  \"Acesso nao autorizado.\"}";
        }
        return "{ \"status\": 401,\"operacao\": \"excluirAviso\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
    }

    public static String cadastrarUsuarioCategoria(String json) throws IOException {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        if (token.isBlank())
            return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
        if (logados.contains(token)) {
            if (admin.contains(token) || token.equals(usuariodata.get("ra").getAsString())) {
                String ra = usuariodata.get("ra").getAsString();
                int categoria = usuariodata.get("categoria").getAsInt();
                boolean hasuser = false;
                boolean hascateg = false;
                for (Usuario usuario : usuarios) {
                    if (usuario.getRA().equals(ra)) {
                        hasuser = true;
                        break;
                    }
                }
                for (Categoria categoria1 : categorias) {
                    if (categoria1.getId() == categoria) {
                        hascateg = true;
                        break;
                    }
                }
                if (hasuser) {
                    if (hascateg) {
                        for (UserCateg userCateg : userCategs) {
                            if (userCateg.getRa().equals(ra)) {
                                userCateg.addCategoria(categoria);
                                ModificadordeArquivos.modifyusercategFile(fileusercategs, userCategs);
                                return "{\"status\": 201 ,\"operacao\": \"cadastrarUsuarioCategoria\",\"mensagem\":  \"Cadastro em categoria realizado com sucesso\"}";

                            }
                        }
                        UserCateg usercategnew = new UserCateg(ra, new int[]{categoria});
                        userCategs.add(usercategnew);
                        ModificadordeArquivos.modifyusercategFile(fileusercategs, userCategs);
                        return "{\"status\": 201 ,\"operacao\": \"cadastrarUsuarioCategoria\",\"mensagem\":  \"Cadastro em categoria realizado com sucesso\"}";
                    } else {
                        return "{\"status\": 401 ,\"operacao\": \"cadastrarUsuarioCategoria\",\"mensagem\":  \"Categoria nao encontrada.\"}";
                    }
                } else {
                    return "{\"status\": 401 ,\"operacao\": \"cadastrarUsuarioCategoria\",\"mensagem\":  \"Usuario nao encontrado.\"}";
                }
            } else {
                return "{\"status\": 401,\"operacao\": \"cadastrarUsuarioCategoria\",\"mensagem\":  \"Acesso nao autorizado.\"}";
            }
        } else {
            return "{\"status\": 401,\"operacao\": \"cadastrarUsuarioCategoria\",\"mensagem\":  \"Acesso nao autorizado.\"}";
        }

    }

    public static String listarUserCateg(String json) {
        int[] categoriasachadas;
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        RetornoListarUserCateg retorno;
        Gson gson = new Gson();
        if (logados.contains(token)) {
            if (token.equals(admin)) {
                if (usuariodata.has("ra")) {
                    String ra = usuariodata.get("ra").getAsString();
                    for (UserCateg uc : userCategs) {
                        if (uc.getRa().equals(ra)) {
                            categoriasachadas = uc.getCategorias();
                            retorno = new RetornoListarUserCateg(categoriasachadas);
                            return gson.toJson(retorno);
                        }
                    }
                    return "{\"status\": 401 ,\"operacao\": \"listarUsuarioCategorias\",\"mensagem\":  \"Usuario nao encontrado.\"}";

                } else {
                    return "{\"status\": 401 ,\",\"mensagem\":  \"Os campos recebidos nao sao validos.\"}";
                }
            } else {
                if (token.equals(usuariodata.get("ra").getAsString()) && usuariodata.has("ra")) {
                    String ra = usuariodata.get("ra").getAsString();
                    for (UserCateg uc : userCategs) {
                        if (uc.getRa().equals(ra)) {
                            categoriasachadas = uc.getCategorias();
                            retorno = new RetornoListarUserCateg(categoriasachadas);
                            return gson.toJson(retorno);
                        }
                    }
                } else {
                    return "{\"status\": 401,\"operacao\": \"listarUsuarioCategorias\",\"mensagem\":  \"Acesso nao autorizado.\"}";
                }
            }
        } else {
            return "{\"status\": 401,\"operacao\": \"listarUsuarioCategorias\",\"mensagem\":  \"Credenciais incorretas.\"}";
        }
        return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
    }

    public static String excluirUsuarioCategoria(String json) throws IOException {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String token = usuariodata.get("token").getAsString();
        if (token.isBlank())
            return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
        if (logados.contains(token)) {
            if (admin.contains(token) || token.equals(usuariodata.get("ra").getAsString())) {
                String ra = usuariodata.get("ra").getAsString();
                int categoria = usuariodata.get("categoria").getAsInt();
                boolean hasuser = false;
                boolean hascateg = false;
                for (Usuario usuario : usuarios) {
                    if (usuario.getRA().equals(ra)) {
                        hasuser = true;
                        break;
                    }
                }
                for (Categoria categoria1 : categorias) {
                    if (categoria1.getId() == categoria) {
                        hascateg = true;
                        break;
                    }
                }
                if (hasuser) {
                    if (hascateg) {
                        for (UserCateg userCateg : userCategs) {
                            if (userCateg.getRa().equals(ra)) {
                                userCateg.removeCategoria(categoria);
                                ModificadordeArquivos.modifyusercategFile(fileusercategs, userCategs);
                                return "{\"status\": 201 ,\"operacao\": \"descadastrarUsuarioCategoria\",\"mensagem\":  \"Descadastro em categoria realizado com sucesso\"}";
                            }
                        }
                        return "{\"status\": 401 ,\"operacao\": \"descadastrarUsuarioCategoria\",\"mensagem\":  \"Usuario nao cadastrado em nenhuma categoria\"}";
                    } else {
                        return "{\"status\": 401 ,\"operacao\": \"descadastrarUsuarioCategoria\",\"mensagem\":  \"Categoria nao encontrada.\"}";
                    }
                } else {
                    return "{\"status\": 401 ,\"operacao\": \"descadastrarUsuarioCategoria\",\"mensagem\":  \"Usuario nao encontrado.\"}";
                }
            } else {
                return "{\"status\": 401,\"operacao\": \"descadastrarUsuarioCategoria\",\"mensagem\":  \"Acesso nao autorizado.\"}";
            }
        } else {
            return "{\"status\": 401,\"operacao\": \"descadastrarUsuarioCategoria\",\"mensagem\":  \"Acesso nao autorizado.\"}";
        }

    }

    public static String listarAvisosUser(String json) {
        JsonObject usuariodata = JsonParser.parseString(json).getAsJsonObject();
        String retorno;
        Gson gson = new Gson();
        ArrayList<Aviso> avisosfiltrados = new ArrayList<>();
        String token = usuariodata.get("token").getAsString();
        if (logados.contains(token)) {
            if (usuariodata.has("ra")) {
                String ra = usuariodata.get("ra").getAsString();
                for (Usuario usuario : usuarios) {
                    if (usuario.getRA().equals(ra)) {
                        for (UserCateg uc : userCategs) {
                            if (uc.getRa().equals(ra)) {
                                int[] categorias = uc.getCategorias();
                                for (int categs : categorias) {
                                    for (Aviso aviso : avisos) {
                                        if (aviso.getCategoria() == categs) {
                                            avisosfiltrados.add(aviso);
                                        }
                                    }
                                }
                                RetornaListarAvisosUsuario retornoj = new RetornaListarAvisosUsuario(avisosfiltrados);
                                return gson.toJson(retornoj);
                            }
                        }
                        return "{\"status\": 401 ,\"operacao\": \"listarUsuarioAvisos\",\"mensagem\":  \"Usuario sem categorias.\"}";
                    }
                }
                return "{\"status\": 401 ,\"operacao\": \"listarUsuarioAvisos\",\"mensagem\":  \"Usuario nao encontrado.\"}";

            } else {
                return "{ \"status\": 401,\"mensagem\":\"Não foi possível possível processar a requisição.\"}";
            }
        }
        return "{\"status\": 401,\"operacao\": \"listarUsuarioAvisos\",\"mensagem\":  \"Credenciais incorretas\"}";

    }
}



