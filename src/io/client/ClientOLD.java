package io.client;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ClientOLD implements Runnable, Serializable {

    private final String terminateMSG = """
            {"type":"terminate"}
            """;

    private int state;

    public ClientOLD() {
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
                                msg = getServerMsg(in);
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
                                msg = getServerMsg(in);
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
                                        System.out.println("AUSWAHL ERFOLGT");
                                        subState = temp * 10 + 10; // handeln der auswahl
                                        shouldSkipInput = true; // erstmal kein input, erstmal nichts senden
                                    } else {
                                        System.out.println("KEINE GÜLTIGE OPTION");
                                    }
                                } else {
                                    System.out.println("EINGABE IST KEINE ZAHL");
                                }
                            }
                            case 10 -> { // mit zufälligem Gegner spielen
                                System.out.println("BEITRETEN DER QUEUE");
                                out.println(String.format("{\"type\":\"modeselect\",\"mode\":%d}", temp));
                            }
                            case 20 -> { // Spiel beitreten
                                System.out.println("UUID DES SPIELS EINGEBEN");
                                subState = 21;
                            }
                            case 21 -> {
                                if (isInteger(input)) {
                                    long uuid = Long.parseLong(input);
                                    out.println(String.format("{\"type\":\"modeselect\",\"mode\":%d,\"uuid\":%d}", temp, uuid));
                                    subState = 22;
                                } else {
                                    System.out.println("EINGABE KEINE ZAHL");
                                    shouldSkipInput = true;
                                    subState = 20; // bisschen hässlich so, aber naja
                                }
                            }
                            case 22 -> {
                                msg = getServerMsg(in);
                                if(msg.getString("type").equals("queueNotification")){
                                    if(msg.getBoolean("ready")){
                                        state = 3;
                                        subState = 0;
                                    } else {
                                        state = 2;
                                        subState = 0;
                                    }
                                } else {
                                    System.out.println("FEHLER IM PROTOKOLL");
                                }
                                // TODO queue handeln: warten oder game starten
                            }
                            case 30 -> { // Spiel erstellen
                                System.out.println("LOBBY ERSTELLT");
                                msg = getServerMsg(in);
                                if(msg.getString("type").equals("uuid")){
                                    System.out.println("UUID IHRER LOBBY: " + msg.getLong("uuid"));
                                    System.out.println("GEBEN SIE DIESE UUID IHREN MITSPIELER. ER KANN DANN DIESER LOBBY BEITRETEN");
                                    state = 2;
                                    subState = 0;
                                } else {
                                    System.out.println("FEHLER IM PROTOKOLL");
                                }
                            }
                        }
                    }
                    case 2 -> { // AUF GEGNER WARTEN
                        switch (subState){
                            case 0 -> { // setup waiting without input
                                Runnable waiter = new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while(!in.ready()){
                                                Thread.sleep(10);
                                            }
                                            System.out.println("SPIELER GEFUNDEN");
                                        } catch (Exception ex){
                                            ex.printStackTrace();
                                            System.out.println("FEHLER IM PROGRAMM");
                                        }
                                    }
                                };
                                Thread waiterThread = new Thread(waiter);
                                waiterThread.start();
                                subState = 1;
                            }
                            case 1 -> { // hier sind wir, wenn input gekommen ist und das nicht EXIT war
                                if(!in.ready()){
                                    System.out.println("KEIN GEGENSPIELER GEFUNDEN. WARTEN SIE WEITER ODER VERLASSEN SIE DAS PROGRAM");
                                } else {
                                    // TODO keine Ahnung den Type ins Spiel tuen oder so?
                                }
                            }
                        }
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
        // ClientOLD gestoppt
        System.out.println("CLIENT GESTOPPT");
    }

    private JSONObject getServerMsg(BufferedReader in) throws IOException, InterruptedException {
        while (!in.ready()) {
            Thread.sleep(10);
        }
        return new JSONObject(in.readLine());
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

    }


}
