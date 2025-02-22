package Servidor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Validador {
    protected String json;
    protected JsonObject jobject;


    public Validador(String json) {
        this.json = json;
        this.jobject = JsonParser.parseString(json).getAsJsonObject();
    }

    public boolean temcamponulo() { //retorna TRUE quando ha campos nulos
        boolean hasNullValues = false;
        for (String key : jobject.keySet()) {
            JsonElement value = jobject.get(key);
            if (value.isJsonNull()) {
                hasNullValues = true;

            }
        }
        return hasNullValues;
    }

    public boolean loginincorreto() { //retorna True quando a campos de login invalidos
        boolean temerro = false;
        if (jobject.has("ra") && jobject.has("senha")) {
            String ra = jobject.get("ra").getAsString();
            String senha = jobject.get("senha").getAsString();
            if (ra.length() == 7 && ra.chars().allMatch(Character::isDigit)) {
                if (senha.matches("[a-zA-Z]{8,20}")) {
                    System.out.println("Dados de login corretos");
                } else {
                    temerro = true;
                }
            } else {
                temerro = true;
            }
        } else {
            temerro = true;
        }
        return temerro;
    }

    public boolean logoutincorreto() {
        boolean temerro = true;
        if (jobject.has("token")) {
            String token = jobject.get("token").getAsString();
            if (token.chars().allMatch(Character::isDigit)) {
                temerro = false;
            }
        }
        return temerro;
    }

    public boolean usuarioinvalido() {
        boolean temerro = true;
        if (jobject.has("ra")) {
            if (jobject.has("nome")) {
                if (jobject.has("senha")) {
                    String senha = jobject.get("senha").getAsString();
                    String nome = jobject.get("nome").getAsString();
                    String ra = jobject.get("ra").getAsString();
                    if (senha.matches("[a-zA-Z]{8,20}") && nome.matches("[A-Z ]{1,50}") && ra.length() == 7 && ra.chars().allMatch(Character::isDigit))
                        temerro = false;
                } else {
                    System.out.println("Não possui senha");
                }
            } else {
                System.out.println("Não possui nome");
            }
        } else {
            System.out.println("Não possui RA");
        }
        return temerro;
    }

    public boolean categoriainvalida() {
        boolean temerro = true;
        if (jobject.has("id")) {
            if (jobject.has("nome")) {
                String nome = jobject.get("nome").getAsString();
                int id = jobject.get("id").getAsInt();
                if (nome.matches("[A-Z ]{1,50}") && (id >= 0)) {
                    temerro = false;
                }
            } else {
                System.out.println("Não possui nome");
            }
        } else {
            System.out.println("Não possui id");
        }
        return temerro;
    }

    public boolean avisoinvalido() {
        boolean temerro = true;
        if (jobject.has("id")) {
            if (jobject.has("titulo")) {
                if (jobject.has("categoria")) {
                    if (jobject.has("descricao")) {
                        String titulo = jobject.get("titulo").getAsString();
                        int categoria = jobject.get("categoria").getAsInt();
                        int id = jobject.get("id").getAsInt();
                        String descricao = jobject.get("descricao").getAsString();
                        if (titulo.matches("[A-Z ]{1,100}") && descricao.matches("[a-zA-Z ]{1,500}") && (id >= 0) && (categoria >= 0)) {
                            temerro = false;
                        }
                    } else {
                        System.out.println("nao possui descricao");
                    }
                } else {
                    System.out.println("nao possui categoria");
                }
            } else {
                System.out.println("Não possui titulo");
            }
        } else {
            System.out.println("Não possui id");
        }
        return temerro;
    }

}
