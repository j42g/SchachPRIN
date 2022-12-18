package io.client;

import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Scanner;

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
            int tempState = 0; // um im loginvorgang oder wo anders den state zu unterscheidgen (muss am Ende wieder auf 0 gesetzt werden?)
            int temp = -1;
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
                        switch (tempState) {
                            case 0 -> { // erste nachricht anzeigen
                                System.out.println("EINLOGGEN (0) ODER REGISTIEREN (1)");
                                tempState = 1;
                            }
                            case 1 -> { // einloggen oder registieren
                                if (isInteger(input)) {
                                    temp = Integer.parseInt(input);
                                    if (temp == 0) { // einloggen
                                        System.out.println("GEGEN SIE IHREN BENUTZERNAME EIN");
                                        tempState = 20;
                                    } else if (temp == 1) { // registrieren
                                        System.out.println("WÄHLEN SIE IHREN BENUTZERNAME EIN");
                                        tempState = 30;
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
                                tempState = 50; // Daten senden
                            }
                            case 30 -> { // registrieren
                                name = input;
                                System.out.println("WÄHLEN SIE IHR PASSWORD");
                                tempState = 50;

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
                                        tempState = 0;
                                    } else { // Fehlgeschlagen
                                        if (temp == 0) {
                                            System.out.println("EINLOGGEN FEHLGESCHLAGEN");
                                        } else if (temp == 1) {
                                            System.out.println("REGISTRIERUNG FEHLGESCHLAGEN");
                                        }
                                        tempState = 0;
                                        shouldSkipInput = true;
                                    }

                                } else { // ??
                                    System.out.println("FEHLER");
                                    return;

                                }
                            }
                        }


                    }
                    case 1 -> { // SPIELMODE AUSWÄHLEN

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

    private void login(ObjectInputStream in, OutputStreamWriter out) {
        boolean loggedIn = false;
        System.out.println("EINLOGGEN (0) ODER REGISTIEREN (1)");
        Scanner s = new Scanner(System.in);
        String input;

        do {
            input = s.nextLine();
        } while (isInteger(input) && (Integer.parseInt(input) == 0 || Integer.parseInt(input) == 1));
        String name;
        String password;
        JSONObject response;
        if (Integer.parseInt(input) == 0) {
            while (!loggedIn) {
                System.out.print("Benutzername: ");
                name = s.nextLine();
                System.out.print("Password: ");
                password = s.nextLine();
                try {
                    out.write(String.format("{\"type\":\"login\",\"name\":\"%s\",\"password\":\"%s\"}", name, hashPassword(password)));
                    do {
                        response = (JSONObject) in.readObject();
                    } while (response == null);
                    if (true) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {

        }
    }

    public void codeDump() {

        Socket client = null;
        try {
            // Make connection
            System.out.println("HEHW");
            client = new Socket("localhost", 7777);

            System.out.println("Verbindung hergestellt. \nWenn Sie die Verbindung schließen wollen geben Sie \"EXIT\" ein");
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write(getArt((String[]) in.readObject()));
        } catch (Exception e) {
            System.out.println("Fehler beim Verbinden");
            e.printStackTrace();
        }
    }

    private void gameLoop() {
    }

    private int getArt(String[] reqTypes) {
        for (int i = 0; i < reqTypes.length; i++) {
            System.out.println(reqTypes[i]);
        }
        System.out.println(reqTypes.length);
        Scanner s = new Scanner(System.in);
        String input;
        int temp;
        while (true) {
            System.out.println("Wählen Sie:");
            for (int i = 0; i < reqTypes.length; i++) {
                System.out.println(reqTypes[i] + "(" + i + ")");
            }

            input = s.next();
            if (!isInteger(input)) {
                System.out.println("Die Eingabe war keine ganze Zahl!");
                continue;
            }
            temp = Integer.parseInt(input);
            if (!(-1 < temp && temp < reqTypes.length)) {
                System.out.println("Die Zahl ist keine gültige Option!");
                continue;
            }
            return temp;
        }
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
