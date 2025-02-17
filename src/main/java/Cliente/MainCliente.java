package Cliente;

import Base.*;
import Servidor.Validador;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class MainCliente {
    public static String token = "";

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
        System.out.println("Conectando a " + serverHostname + " na porta " + serverport);
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
            System.err.println("Couldn't get I/O for " + "the connection to: " + serverHostname);
            System.exit(1);
        }


        while (continuar) {

            System.out.println("""
                    ====
                    QUAL ACAO DESEJA?
                    ====
                      1 - Login
                      2 - Cadastro
                      3 - Listar usuarios
                      4 - Buscar Usuario
                      5 - Editar Usuario
                      6 - Excluir Usuario
                      7 - Salvar Categoria
                      8 - Listar Categorias
                      9 - Localizar Categoria
                      10 - Excluir Categoria
                      11 - Salvar Aviso
                      12 - Listar Avisos
                      13 - Localizar Aviso
                      14 - Excluir Aviso
                      15 - Cadastrar Usuario em uma categoria
                      16 - Listar categorias em que usuario esta cadastrado
                      17 - Descadastrar Usuario em categoria
                      18 - Receber avisos
                      20 - logout
                      0 - fechar""");
            userinput = scan.nextLine();
            switch (userinput) {
                case "1":
                    MainCliente.efetuarlogin(scan, gson, out, in);
                    break;
                case "2":
                    MainCliente.efetuarCadastro(scan, gson, out, in);
                    break;
                case "3":
                    MainCliente.listarUsusario(gson, out, in);
                    break;
                case "4":
                    MainCliente.localizarusuario(scan, gson, out, in);
                    break;
                case "5":
                    MainCliente.atualizarcadastro(scan, gson, out, in);
                    break;
                case "6":
                    MainCliente.excluirusuario(scan, gson, out, in);
                    break;
                case "7":
                    MainCliente.salvarCategoria(scan, gson, out, in);
                    break;
                case "8":
                    MainCliente.listarCategorias(gson, out, in);
                    break;
                case "9":
                    MainCliente.localizarcategoria(scan, gson, out, in);
                    break;
                case "10":
                    MainCliente.excluircategoria(scan, gson, out, in);
                    break;
                case "11":
                    MainCliente.salvarAvisos(scan, gson, out, in);
                    break;
                case "12":
                    MainCliente.listarAvisos(scan, gson, out, in);
                    break;
                case "13":
                    MainCliente.localizaraviso(scan, gson, out, in);
                    break;
                case "14":
                    MainCliente.excluiraviso(scan, gson, out, in);
                    break;
                case "15":
                    MainCliente.cadastrarusercateg(scan, gson, out, in);
                    break;
                case "16":
                    MainCliente.listarUsusarioCategoria(scan, gson, out, in);
                    break;
                case "17":
                    MainCliente.descadastrarusercateg(scan, gson, out, in);
                    break;
                case "18":
                    MainCliente.listaravisosdousuariomanual(scan, gson, out, in);
                    break;
                case "20":
                    MainCliente.efetuarlogout(gson, out, in);
                    break;
                case "0":
                    System.out.println("fechando aplicacao");
                    continuar = false;
                    break;
                default:
                    System.out.println("Opcao invalida\n");
            }
            System.out.println("Aperte qualquer tecla");
            scan.nextLine();

        }
        out.close();
        in.close();
        soquete.close();
    }

    protected static void efetuarlogin(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("====\nInsira seu RA:");
            String ra = scan.nextLine();
            String retorno;
            System.out.println("Insira sua senha:");
            String senha = scan.nextLine();
            Login login = new Login(ra, senha);
            String json = gson.toJson(login);
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            if (retornojson.get("status").getAsInt() == 200) {
                System.out.println("Login efetuado");
                token = retornojson.get("token").getAsString();
                listaravisosdousuario(out, gson, in, token);

            } else {
                System.out.println(retornojson.get("mensagem").getAsString());

            }
        } else {
            System.out.println("Ainda esta logado");
        }

    }


    protected static void efetuarCadastro(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("====\nInsira seu RA (7 digitos):");
            String ra = scan.nextLine();
            System.out.println("Insira seu nome (Apenas letrasmaiusculas maiusculas):");
            String nome = scan.nextLine();
            String retorno;
            System.out.println("Insira sua senha (Entre 8 e 50 letras):");
            String senha = scan.nextLine();
            Usuario usuario = new Usuario(ra, nome, senha);
            String json = gson.toJson(usuario);
            Validador valid = new Validador(json);
            if (!(valid.usuarioinvalido())) {
                Cadastro cad = new Cadastro(ra, senha, nome);
                json = gson.toJson(cad);
                System.out.println("Enviando ao server:" + json);
                out.println(json);
                retorno = in.readLine();
                System.out.println("Recebido do Server: " + retorno);
                JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
                if (retornojson.get("status").getAsInt() == 200) {
                    System.out.println("Cadastro efetuado");
                    token = retornojson.get("token").getAsString();
                } else {
                    System.out.println(retornojson.get("mensagem").getAsString());

                }
            } else {
                System.out.println("Dados de usuario invalidos\n");
            }
        } else {
            System.out.println("Ainda esta logado");
        }
    }

    protected static void efetuarlogout(Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            Logout logout = new Logout(token);
            String json = gson.toJson(logout);
            String retorno;
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            if (retornojson.get("status").getAsInt() == 200) {
                System.out.println("Logout efetuado");
                token = "";
            } else {
                System.out.println(retornojson.get("mensagem").getAsString());

            }

        }
    }

    protected static void listarUsusario(Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            Listarusuario listar = new Listarusuario(token);
            String json = gson.toJson(listar);
            String retorno;
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            if (retornojson.get("status").getAsInt() == 201) {
                JsonArray lista = retornojson.getAsJsonArray("usuario");
                Usuario[] userArray = gson.fromJson(lista, Usuario[].class);
                System.out.println("Lista de Usuarios:");
                for (Usuario user : userArray) {
                    System.out.println(user);
                }
            } else {
                System.out.println(retornojson.get("mensagem").getAsString());

            }
        }
    }

    protected static void localizarusuario(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            System.out.println("====\nInsira o RA desejado (nao administradores nao podem acessar outros usuarios):");
            String ra = scan.nextLine();
            LocalizarUsuario listar = new LocalizarUsuario(token, ra);
            String json = gson.toJson(listar);
            String retorno;
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            if (retornojson.get("status").getAsInt() == 201) {
                JsonObject lista = retornojson.getAsJsonObject("usuario");
                Usuario userfound = gson.fromJson(lista, Usuario.class);
                System.out.println("Usuario encontrado: \n" + userfound);
            } else {
                System.out.println(retornojson.get("mensagem").getAsString());

            }
        }
    }

    protected static void excluirusuario(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            System.out.println("====\nInsira o RA desejado (apenas ADM):");
            String ra = scan.nextLine();
            ExcluirUsuario listar = new ExcluirUsuario(token, ra);
            String json = gson.toJson(listar);
            String retorno;
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            if (retornojson.get("status").getAsInt() == 201) {
                System.out.println("Usuario Deletado!");
            } else {
                System.out.println(retornojson.get("mensagem").getAsString());

            }
        }
    }

    protected static void atualizarcadastro(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            System.out.println("====\nInsira o RA (7 digitos):");
            String ra = scan.nextLine();
            System.out.println("Insira seu nome (Apenas letrasmaiusculas maiusculas):");
            String nome = scan.nextLine();
            String retorno;
            System.out.println("Insira sua senha (Entre 8 e 50 letras):");
            String senha = scan.nextLine();
            Usuario usuario = new Usuario(ra, nome, senha);
            String json = gson.toJson(usuario);
            Validador valid = new Validador(json);
            if (!(valid.usuarioinvalido())) {
                EditarUsuario cad;
                cad = new EditarUsuario(usuario, token);
                json = gson.toJson(cad);
                System.out.println("Enviando ao server:" + json);
                out.println(json);
                retorno = in.readLine();
                System.out.println("Recebido do Server: " + retorno);
                JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
                System.out.println(retornojson.get("mensagem").getAsString());
            } else {
                System.out.println("Dados de usuario invalidos");
            }
        }
    }

    protected static void salvarCategoria(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (!token.isEmpty()) {
            System.out.println("====\nInsira o id da cataegoria ou insira 0 para criar nova:");
            int id = scan.nextInt();
            scan.skip("\n");
            System.out.println("Insira o nome da categoria (Apenas letrasmaiusculas maiusculas):");
            String nome = scan.nextLine();
            String retorno;
            Categoria categ = new Categoria(id, nome);
            String json = gson.toJson(categ);
            Validador valid = new Validador(json);
            if (!(valid.categoriainvalida())) {
                SalvarCategoria cad = new SalvarCategoria(token, categ);
                json = gson.toJson(cad);
                System.out.println("Enviando ao server:" + json);
                out.println(json);
                retorno = in.readLine();
                System.out.println("Recebido do Server: " + retorno);
                JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
                if (retornojson.get("status").getAsInt() == 200) {
                    System.out.println("Salvamento efetuado");
                } else {
                    System.out.println(retornojson.get("mensagem").getAsString());

                }
            } else {
                System.out.println("Dados de categoria invalidos\n");
            }
        } else {
            System.out.println("Nao esta logado");
        }
    }

    protected static void listarCategorias(Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            ListarCategorias listar = new ListarCategorias(token);
            String json = gson.toJson(listar);
            String retorno;
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            if (retornojson.get("status").getAsInt() == 201) {
                JsonArray lista = retornojson.getAsJsonArray("categorias");
                Categoria[] userArray = gson.fromJson(lista, Categoria[].class);
                System.out.println("Lista de Categorias:");
                for (Categoria categ : userArray) {
                    System.out.println(categ);
                }
            } else {
                System.out.println(retornojson.get("mensagem").getAsString());

            }
        }
    }

    protected static void localizarcategoria(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            System.out.println("====\nInsira o ID desejado:");
            int id = scan.nextInt();
            scan.skip("\n");
            LocalizarCategoria listar = new LocalizarCategoria(token, id);
            String json = gson.toJson(listar);
            String retorno;
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            if (retornojson.get("status").getAsInt() == 201) {
                JsonObject lista = retornojson.getAsJsonObject("categoria");
                Categoria userfound;
                userfound = gson.fromJson(lista, Categoria.class);
                System.out.println("Categoria encontrada: \n" + userfound);
            } else {
                System.out.println(retornojson.get("mensagem").getAsString());

            }
        }
    }

    protected static void excluircategoria(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            System.out.println("====\nInsira o id desejado (apenas ADM):");
            int id = scan.nextInt();
            scan.skip("\n");
            ExcluirCategoria listar = new ExcluirCategoria(token, id);
            String json = gson.toJson(listar);
            String retorno;
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            System.out.println(retornojson.get("mensagem").getAsString());
        }
    }

    protected static void salvarAvisos(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (!token.isEmpty()) {
            System.out.println("====\nInsira o id do aviso ou insira 0 para criar nova:");
            int id = scan.nextInt();
            scan.skip("\n");
            System.out.println("Insira o id da categoria:");
            int categoria = scan.nextInt();
            scan.skip("\n");
            System.out.println("Insira o Titulo do aviso (Apenas letrasmaiusculas maiusculas):");
            String titulo = scan.nextLine();
            System.out.println("Insira a descricao (maximo 500 caracteres):");
            String descricao = scan.nextLine();
            String retorno;
            Aviso aviso = new Aviso(id, categoria, titulo, descricao);
            String json = gson.toJson(aviso);
            Validador valid = new Validador(json);
            if (!(valid.avisoinvalido())) {
                SalvarAviso cad = new SalvarAviso(token, aviso);
                json = gson.toJson(cad);
                System.out.println("Enviando ao server:" + json);
                out.println(json);
                retorno = in.readLine();
                System.out.println("Recebido do Server: " + retorno);
                JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
                if (retornojson.get("status").getAsInt() == 200) {
                    System.out.println("Salvamento efetuado");
                } else {
                    System.out.println(retornojson.get("mensagem").getAsString());

                }
            } else {
                System.out.println("Dados de aviso invalidos\n");
            }
        } else {
            System.out.println("Nao esta logado");
        }
    }

    protected static void listarAvisos(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            System.out.println("Insira o id da categoria a se listar, ou 0 para lista todas:");
            int categoria = scan.nextInt();
            scan.skip("\n");
            ListarAvisos listar = new ListarAvisos(token, categoria);
            String json = gson.toJson(listar);
            String retorno;
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            if (retornojson.get("status").getAsInt() == 201) {
                JsonArray lista = retornojson.getAsJsonArray("avisos");
                Aviso[] userArray = gson.fromJson(lista, Aviso[].class);
                System.out.println("Lista de Categorias:");
                for (Aviso aviso : userArray) {
                    System.out.println(aviso);
                }
            } else {
                System.out.println(retornojson.get("mensagem").getAsString());

            }
        }
    }

    protected static void localizaraviso(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            System.out.println("====\nInsira o ID desejado:");
            int id = scan.nextInt();
            scan.skip("\n");
            LocalizarAvisos listar = new LocalizarAvisos(token, id);
            String json = gson.toJson(listar);
            String retorno;
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            if (retornojson.get("status").getAsInt() == 201) {
                JsonObject lista = retornojson.getAsJsonObject("aviso");
                Aviso avisofound = gson.fromJson(lista, Aviso.class);
                System.out.println("Aviso Encontrado: \n" + avisofound);
            } else {
                System.out.println(retornojson.get("mensagem").getAsString());

            }
        }
    }

    protected static void excluiraviso(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            System.out.println("====\nInsira o id desejado (apenas ADM):");
            int id = scan.nextInt();
            scan.skip("\n");
            ExcluirAviso listar = new ExcluirAviso(token, id);
            String json = gson.toJson(listar);
            String retorno;
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            System.out.println(retornojson.get("mensagem").getAsString());
        }
    }

    protected static void cadastrarusercateg(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (!token.isEmpty()) {
            System.out.println("====\nInsira o ra:");
            String ra = scan.nextLine();
            System.out.println("Insira o id da cataegoria:");
            int categ = scan.nextInt();
            scan.skip("\n");
            String retorno;
            CadastrarUsuarioCategoria cad = new CadastrarUsuarioCategoria(token, ra, categ);
            String json = gson.toJson(cad);
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            System.out.println(retornojson.get("mensagem").getAsString());
        } else {
            System.out.println("Nao esta logado");
        }
    }

    protected static void listarUsusarioCategoria(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (token.isEmpty()) {
            System.out.println("Nao esta logado ainda");
        } else {
            System.out.println("====\nInsira o RA desejado (nao administradores nao podem acessar outros usuarios):");
            String ra = scan.nextLine();
            ListarUserCateg listar = new ListarUserCateg(token, ra);
            String json = gson.toJson(listar);
            String retorno;
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            if (retornojson.get("status").getAsInt() == 201) {
                JsonArray lista = retornojson.getAsJsonArray("categorias");
                int[] userfound = gson.fromJson(lista, int[].class);
                System.out.println("Categorias do Usuario encontradas: \n" + Arrays.toString(userfound));
            } else {
                System.out.println(retornojson.get("mensagem").getAsString());

            }
        }
    }

    protected static void descadastrarusercateg(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (!token.isEmpty()) {
            System.out.println("====\nInsira o ra:");
            String ra = scan.nextLine();
            System.out.println("Insira o id da cataegoria:");
            int categ = scan.nextInt();
            scan.skip("\n");
            String retorno;
            DescadastrarUserCateg cad = new DescadastrarUserCateg(token, ra, categ);
            String json = gson.toJson(cad);
            System.out.println("Enviando ao server:" + json);
            out.println(json);
            retorno = in.readLine();
            System.out.println("Recebido do Server: " + retorno);
            JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
            System.out.println(retornojson.get("mensagem").getAsString());
        } else {
            System.out.println("Nao esta logado");
        }
    }

    public static void listaravisosdousuariomanual(Scanner scan, Gson gson, PrintWriter out, BufferedReader in) throws IOException {
        if (!token.isEmpty()) {
            System.out.println("====\nInsira o ra:");
            String ra = scan.nextLine();
            listaravisosdousuario(out, gson, in, ra);
        } else {
            System.out.println("Nao esta logado");
        }

    }

    public static void listaravisosdousuario(PrintWriter out, Gson gson, BufferedReader in, String ra) throws IOException {
        ListarUserAvisos envio = new ListarUserAvisos(token, ra);
        String json = gson.toJson(envio);
        String retorno;
        System.out.println("Enviando ao server:" + json);
        out.println(json);
        retorno = in.readLine();
        System.out.println("Recebido do Server: " + retorno);
        JsonObject retornojson = JsonParser.parseString(retorno).getAsJsonObject();
        if (retornojson.get("status").getAsInt() == 201) {
            JsonArray lista = retornojson.getAsJsonArray("avisos");
            Aviso[] userArray = gson.fromJson(lista, Aviso[].class);
            System.out.println("Lista de Categorias:");
            for (Aviso aviso : userArray) {
                System.out.println(aviso);
            }
        } else {
            System.out.println(retornojson.get("mensagem").getAsString());

        }

    }


}
