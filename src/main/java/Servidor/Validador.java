package Servidor;

import com.google.gson.*;

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
}