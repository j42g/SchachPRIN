package io.client;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ClientInputHandler implements Runnable {

    private final String[] alleBefehle = new String[]{"VERBINDEN", "EXIT", "TRENNEN", "ANMELDEN", "REGISTRIEREN", "EXIT", "SPIELMODI", "AUFGEBEN", "VERLASSEN"};

    private Verbinder v;

    private boolean verbunden;
    private boolean eingeloggt;
    private boolean imSpiel;
    private boolean amZug;
    private boolean spielVorbei;

    public ClientInputHandler(){
        this.v = null;
        this.verbunden = false;
        this.eingeloggt = false;
        this.imSpiel = false;
        this.amZug = false;
        this.spielVorbei = false;
    }

    @Override
    public void run(){
        Scanner s = new Scanner(System.in);
        String input;
        System.out.print("STARTE CLIENT. BEFEHLE: " + alleBefehle[0]);
        ArrayList<String> verfuegbareBefehle = new ArrayList<String>();
        for(int i = 1; i < alleBefehle.length; i++){
            System.out.print(", " + alleBefehle[i]);
        }
        while(true){
            input = s.nextLine().toUpperCase();
            if(!Arrays.asList(this.alleBefehle).contains(input)){
                System.out.println("UNBEKANNTER BEFEHL");
                continue;
            }

            // LOGIK
            if(!verbunden){
                if(input.equals("VERBINDEN")){
                    verbinde();
                } else if(input.equals("EXIT")){
                    return;
                }
            } else { // VERBUNDEN
                if(!eingeloggt){
                    if(input.equals("TRENNEN")){
                        trenne();
                    } else if(input.equals("ANMELDEN")){
                        anmelden();
                    } else if(input.equals("REGISTRIEREN")){
                        registrieren();
                    }
                } else { // EINGELOGGT
                    if (!imSpiel) {
                        if(input.equals("ABMELDEN")){
                            abmelden();
                        } else if(input.equals("SPIELMODI")){
                            // TODO
                        }
                    } else { // IM SPIEL
                        if(spielVorbei){
                            if(input.equals("SPIELMODI")){
                                // TODO
                            }
                        } else { // nicht vorbei
                            if(input.equals("AUFGEBEN")){
                                // TODO
                            } else if(input.equals("VERLASSEN")){
                                // TODO
                            }
                            if(amZug){
                                // TODO
                            } else { // nicht am Zug
                                System.out.println("SIE SIND NICHT AM ZUG");
                            }
                        }
                    }
                }
            }

            // --------------- Welche Befehle kann man verwenden ----------------
            verfuegbareBefehle.clear();
            if(!verbunden){
                verfuegbareBefehle.add("VERBINDEN");
                verfuegbareBefehle.add("EXIT");
            } else { // VERBUNDEN
                if(!eingeloggt){
                    verfuegbareBefehle.add("ANMELDEN");
                    verfuegbareBefehle.add("REGISTRIEREN");
                    verfuegbareBefehle.add("TRENNEN");
                } else { // EINGELOGGT
                    if (!imSpiel) {
                        verfuegbareBefehle.add("ABMELDEN");
                        verfuegbareBefehle.add("SPIELMODI");
                    } else { // IM SPIEL
                        if(spielVorbei){
                            verfuegbareBefehle.add("SPIELMODI");
                        } else { // nicht vorbei
                            verfuegbareBefehle.add("VERLASSEN");
                            verfuegbareBefehle.add("AUFGEBEN");
                            if(amZug){
                                verfuegbareBefehle.add("[ZUG]");
                            }
                        }
                    }
                }
            }
            System.out.print("VERFÜGBARE BEFEHLE: " + verfuegbareBefehle.get(0));
            for(int i = 1; i < alleBefehle.length; i++){
                System.out.print(", " + verfuegbareBefehle.get(i));
            }
        }
    }

    private void trenne() {
        this.verbunden = false;
        this.v.trenne();
        this.v = null;
        System.out.println("VERBINDUNG GETRENNT");
    }

    private void anmelden() {
        Scanner s = new Scanner(System.in);
        String benutzername;
        String password;
        JSONObject antwort;
        System.out.println("VORGANG ABBRECHEN: \"ABBRECHEN\"");
        while(true){
            do {
                System.out.println("GEGEN SIE IHREN BENUTZERNAME EIN");
                benutzername = s.nextLine();
                if (benutzername.equalsIgnoreCase("ABBRECHEN")) {
                    System.out.println("VORGANG ABGEBROCHEN");
                    return;
                }
                System.out.println("GEBEN SIE IHR PASSWORT EIN");
                password = s.nextLine();
                if (password.equalsIgnoreCase("ABBRECHEN")) {
                    System.out.println("VORGANG ABGEBROCHEN");
                    return;
                }
                System.out.println("Benutzername:\t" + benutzername + "\nPasswort:\t" + password);
                System.out.println("WENN SIE DIESE INFORMATIONEN BESTÄTIGEN WOLLEN, GEBEN SIE \"BESTÄTIGEN\" EIN.");
            } while (!s.nextLine().equals("BESTÄTIGEN"));
            String hashedpw = hashPassword(password);
            v.sendeJSON(new JSONObject(String.format("{\"type\":\"login\",\"name\":\"%s\",\"password\":%s}", benutzername, hashedpw)));
            antwort = v.warteAufJSON();
            if(antwort.getString("type").equals("authresponse")){
                if(antwort.getBoolean("success")){
                    System.out.println("ERFOLGREICH ANGEMELDET");
                    this.eingeloggt = true;
                    return;
                } else {
                    System.out.println("FEHLER BEIM ANMELDEN. FEHLER: " + antwort.getString("error"));
                }
            } else {
                System.out.println("FEHLER IM PROTOKOLL");
            }
        }
    }

    public void registrieren(){
        Scanner s = new Scanner(System.in);
        String benutzername;
        String password;
        JSONObject antwort;
        System.out.println("VORGANG ABBRECHEN MIT: \"ABBRECHEN\"");
        while(true){
            do {
                System.out.println("WÄHLEN SIE EINEN BENUTZERNAME");
                benutzername = s.nextLine();
                if (benutzername.equalsIgnoreCase("ABBRECHEN")) {
                    System.out.println("VORGANG ABGEBROCHEN");
                    return;
                }
                System.out.println("WÄHLEN SIE EIN PASSWORT");
                password = s.nextLine();
                if (password.equalsIgnoreCase("ABBRECHEN")) {
                    System.out.println("VORGANG ABGEBROCHEN");
                    return;
                }
                System.out.println("Benutzername:\t" + benutzername + "\nPasswort:\t" + password);
                System.out.println("WENN SIE DIESE INFORMATIONEN BESTÄTIGEN WOLLEN, GEBEN SIE \"BESTÄTIGEN\" EIN.");
            } while (!s.nextLine().equals("BESTÄTIGEN"));
            String hashedpw = hashPassword(password);
            v.sendeJSON(new JSONObject(String.format("{\"type\":\"register\",\"name\":\"%s\",\"password\":%s}", benutzername, hashedpw)));
            antwort = v.warteAufJSON();
            if(antwort.getString("type").equals("authresponse")){
                if(antwort.getBoolean("success")){
                    System.out.println("ERFOLGREICH REGISTRIERT");
                    this.eingeloggt = true;
                    return;
                } else {
                    System.out.println("FEHLER BEIM REGISTRIEREN. FEHLER: " + antwort.getString("error"));
                }
            } else {
                System.out.println("FEHLER IM PROTOKOLL");
            }
        }
    }

    public void abmelden(){
        System.out.println("ABMELDEN..");
        v.sendeJSON(new JSONObject("{\"type\":\"logout\"}"));
        JSONObject response = v.warteAufJSON();
        if(response.getString("type").equals("logoutresponse")){
            System.out.println("ABMELDUNG ERFOLGREICH");
            this.eingeloggt = false;
        } else {
            System.out.println("FEHLER IM PROTOKOLL");
        }
    }

    private void verbinde() {
        this.verbunden = true;
        this.v = Verbinder.getInstance();
    }

    private String hashPassword(String password) {
        byte[] hashedPw;
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");
            hashedPw = hasher.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        StringBuilder jsonArr = new StringBuilder();
        jsonArr.append("[");
        jsonArr.append(hashedPw[0]);
        for(int i = 1; i < hashedPw.length; i++){
            jsonArr.append(",");
            jsonArr.append(hashedPw[i]);
        }
        jsonArr.append("]");
        return jsonArr.toString();
    }

    public static void main(String[] args){
        ClientInputHandler client = new ClientInputHandler();
        Thread clientThread = new Thread(client);
        clientThread.start();
    }

}
