package io.client;

import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Client implements Runnable, Serializable {

    private final String terminateMSG = """
            {"type":"terminate"}
            """;

    private int state;

    public Client() {
        this.state = 0;
    }

    @Override
    public void run() {
        Scanner s = new Scanner(System.in);
        try (Socket server = new Socket("127.0.0.1", 7777);
             BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
             PrintWriter out = new PrintWriter(server.getOutputStream())) {
            JSONObject msg = null;
            String input = null;
            int subState = 0; // um im loginvorgang oder wo anders den state zu unterscheidgen (muss am Ende wieder auf 0 gesetzt werden?)
            int temp = -1;
            int max = -1;
            String name = null;
            String password = null;
            boolean shouldSkipInput = true;
            System.out.println("STARTE CLIENT. SIE KÖNNEN JEDERZEIT DURCH \"EXIT\" DEN CLIENT STOPPEN");
            while (true) {
                if(shouldSkipInput){
                    shouldSkipInput = false;
                } else {
                    input = s.nextLine();
                    if (input.equals("EXIT")) {
                        out.println(terminateMSG);
                        break;
                    }
                }

                switch (state) {
                    case 0 -> { // AUTHO
                        switch (subState) {
                            case 0 -> { // erste nachricht anzeigen
                                System.out.println("EINLOGGEN (0) ODER REGISTIEREN (1)");
                                subState = 1;
                            }
                            case 1 -> { // einloggen oder registieren
                                if (isInteger(input)) {
                                    temp = Integer.parseInt(input);
                                    if (temp == 0) { // einloggen
                                        System.out.println("GEGEN SIE IHREN BENUTZERNAME EIN");
                                        subState = 20;
                                    } else if (temp == 1) { // registrieren
                                        System.out.println("WÄHLEN SIE IHREN BENUTZERNAME EIN");
                                        subState = 30;
                                    } else {
                                        System.out.println("EINGABE MUSS 0 ODER 1 SEIN");
                                    }
                                } else {
                                    System.out.println("EINGABE IST KEINE ZAHL");
                                }
                            }
                            case 20 -> { // einloggen benutzername
                                name = input;
                                System.out.println("GEGEN SIE IHR PASSWORD EIN");
                                subState = 50; // Daten senden
                            }
                            case 30 -> { // registrieren
                                name = input;
                                System.out.println("WÄHLEN SIE IHR PASSWORD");
                                subState = 50;

                            }
                            case 50 -> { // Daten senden
                                password = input;
                                if (temp == 0) { // einloggen
                                    out.println(String.format("{\"type\":\"login\",\"name\":\"%s\",\"password\":%s}", name, hashPassword(password)));
                                } else if (temp == 1) { // registrieren
                                    out.println(String.format("{\"type\":\"register\",\"name\":\"%s\",\"password\":%s}", name, hashPassword(password)));
                                }
                                out.flush();
                                while (!in.ready()) {// auf antwort warten
                                    Thread.sleep(10);
                                }
                                msg = new JSONObject(in.readLine());
                                if (msg.get("type").equals("authresponse")) {
                                    if (msg.getBoolean("success")) { // Erfolgreich
                                        if (temp == 0) {
                                            System.out.println("ERFOLGREICH EINGELOGGT");
                                        } else if (temp == 1) {
                                            System.out.println("ERFOLGREICH REGISTRIERT");
                                        }
                                        state = 1;
                                        subState = 0;
                                    } else { // Fehlgeschlagen
                                        if (temp == 0) {
                                            System.out.println("EINLOGGEN FEHLGESCHLAGEN");
                                        } else if (temp == 1) {
                                            System.out.println("REGISTRIERUNG FEHLGESCHLAGEN");
                                        }
                                        subState = 0;
                                        shouldSkipInput = true;
                                    }

                                } else {
                                    System.out.println("FEHLER IM PROTOKOLL");
                                }
                            }
                        }
                    }
                    case 1 -> { // SPIELMODE AUSWÄHLEN
                        switch (subState){
                            case 0 -> { // empfangen + ausgeben
                                System.out.print("SPIELMODUS AUSWÄHLEN: ");
                                while (!in.ready()) {// auf antwort warten (sollte eh schon da sein)
                                    Thread.sleep(10);
                                }
                                msg = new JSONObject(in.readLine());
                                if(msg.getString("type").equals("options")){
                                    max = msg.getInt("max"); // in case 1 wirds gebraucht
                                    StringBuilder options = new StringBuilder();
                                    Iterator<Object> it = msg.getJSONArray("options").iterator();
                                    for (int i = 0; it.hasNext(); i++) {
                                        options.append((String)it.next());
                                        options.append("(").append(i).append(")");
                                    }
                                    System.out.println(options);
                                    subState = 1;
                                } else {
                                    System.out.println("FEHLER IM PROTKOLL");
                                }
                            }
                            case 1 -> { // wählen
                                if (isInteger(input)) {
                                    temp = Integer.parseInt(input);
                                    if (-1 < temp && temp < max) {
                                        out.println(String.format("{\"type\":\"modeselect\",\"mode\":%d}", temp));
                                        System.out.println("AUSWAHL ERFOLGT");
                                        // TODO uuid abfragen oder auf server warten
                                    } else {
                                        System.out.println("KEINE GÜLTIGE OPTION");
                                    }
                                } else {
                                    System.out.println("EINGABE IST KEINE ZAHL");
                                }
                            }
                        }

                    }
                    case 2 -> { // AUF GEGNER WARTEN

                    }
                    case 3 -> { // IM SPIEL

                    }
                    case 4 -> {

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Client gestoppt
        System.out.println("CLIENT GESTOPPT");
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

    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Client c = new Client();
        Thread cThread = new Thread(c);
        cThread.start();
    }


}
