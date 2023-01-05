package io.client;

import io.Logger;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client implements Runnable {

    private final String[] alleSpielmodi = new String[]{"RANDOM GEGNER", "PRIVATES SPIEL ERSTELLEN", "PRIVATEM SPIEL BEITRETEN"};
    private final String[] alleBefehle = new String[]{"VERBINDEN", "EXIT", "TRENNEN", "ANMELDEN", "REGISTRIEREN", "ABMELDEN", "EXIT", "SPIELMODI", "AUFGEBEN", "VERLASSEN"};

    private Verbinder v;

    private boolean verbunden;
    private boolean eingeloggt;
    private boolean imSpiel;
    private boolean amZug;
    private boolean spielVorbei;

    public Client() {
        this.v = null;
        this.verbunden = false;
        this.eingeloggt = false;
        this.imSpiel = false;
        this.amZug = false;
        this.spielVorbei = false;
    }

    @Override
    public void run() {
        Scanner s = new Scanner(System.in);
        String input;
        System.out.print("STARTE CLIENT. BEFEHLE: " + alleBefehle[0]);
        Logger.log("client", "Starte Client");
        ArrayList<String> verfuegbareBefehle = new ArrayList<String>();
        for (int i = 1; i < alleBefehle.length; i++) {
            System.out.print(", " + alleBefehle[i]);
        }
        System.out.println("\nVERFÜGBARE BEFEHLE: VERBINDEN, EXIT");
        while (true) {
            input = s.nextLine().toUpperCase();
            if (!Arrays.asList(this.alleBefehle).contains(input)) {
                System.out.println("UNBEKANNTER BEFEHL");
                continue;
            }

            // LOGIK
            if (!verbunden) {
                if (input.equals("VERBINDEN")) {
                    verbinde();
                } else if (input.equals("EXIT")) {
                    return;
                }
            } else { // VERBUNDEN
                if (!eingeloggt) {
                    if (input.equals("TRENNEN")) {
                        trenne();
                    } else if (input.equals("ANMELDEN")) {
                        anmelden();
                    } else if (input.equals("REGISTRIEREN")) {
                        registrieren();
                    }
                } else { // EINGELOGGT
                    if (!imSpiel) {
                        if (input.equals("ABMELDEN")) {
                            abmelden();
                        } else if (input.equals("SPIELMODI")) {
                            spielmodiAuswahl();
                        } else if (input.equals("SPIELREGELN")) {
                            // TODO Til
                        } else if (input.equals("RANGLISTE")) {
                            rangliste();
                        }
                    } else { // IM SPIEL
                        if (spielVorbei) {
                            if (input.equals("SPIELMODI")) {
                                spielmodiAuswahl();
                            }
                        } else { // nicht vorbei
                            if (input.equals("AUFGEBEN")) {
                                v.sendeJSON(new JSONObject("{\"type\":\"forfeit\"}"));
                            }
                            if (amZug) {
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
            if (!verbunden) {
                verfuegbareBefehle.add("VERBINDEN");
                verfuegbareBefehle.add("EXIT");
            } else { // VERBUNDEN
                if (!eingeloggt) {
                    verfuegbareBefehle.add("ANMELDEN");
                    verfuegbareBefehle.add("REGISTRIEREN");
                    verfuegbareBefehle.add("TRENNEN");
                } else { // EINGELOGGT
                    if (!imSpiel) {
                        verfuegbareBefehle.add("ABMELDEN");
                        verfuegbareBefehle.add("SPIELMODI");
                        verfuegbareBefehle.add("SPIELREGELN");
                        verfuegbareBefehle.add("RANGLISTE");
                    } else { // IM SPIEL
                        if (spielVorbei) {
                            verfuegbareBefehle.add("SPIELMODI");
                        } else { // nicht vorbei
                            verfuegbareBefehle.add("VERLASSEN");
                            verfuegbareBefehle.add("AUFGEBEN");
                            if (amZug) {
                                verfuegbareBefehle.add("[ZUG]");
                            }
                        }
                    }
                }
            }
            System.out.print("VERFÜGBARE BEFEHLE: " + verfuegbareBefehle.get(0));
            for (int i = 1; i < verfuegbareBefehle.size(); i++) {
                System.out.print(", " + verfuegbareBefehle.get(i));
            }
            System.out.println();
        }
    }

    private void verbinde() {
        this.verbunden = true;
        this.v = Verbinder.getInstance();
        Logger.log("client", "Verbindung zu Server aufgebaut");
    }

    private void trenne() {
        this.verbunden = false;
        this.v.trenne();
        this.v = null;
        System.out.println("VERBINDUNG GETRENNT");
        Logger.log("client", "Verbindung zum Server getrennt");
    }

    private void anmelden() {
        Logger.log("client", "Starte Anmeldevorgang");
        Scanner s = new Scanner(System.in);
        String benutzername;
        String password;
        JSONObject antwort;
        System.out.println("VORGANG ABBRECHEN: \"ABBRECHEN\"");
        while (true) {
            do {
                System.out.println("GEGEN SIE IHREN BENUTZERNAME EIN");
                benutzername = s.nextLine();
                if (benutzername.equalsIgnoreCase("ABBRECHEN")) {
                    System.out.println("VORGANG ABGEBROCHEN");
                    Logger.log("client", "Anmeldevorgange abgebrochen");
                    return;
                }
                System.out.println("GEBEN SIE IHR PASSWORT EIN");
                password = s.nextLine();
                if (password.equalsIgnoreCase("ABBRECHEN")) {
                    System.out.println("VORGANG ABGEBROCHEN");
                    Logger.log("client", "Anmeldevorgange abgebrochen");
                    return;
                }
                System.out.println("Benutzername:\t" + benutzername + "\nPasswort:\t" + password);
                System.out.println("WENN SIE DIESE INFORMATIONEN BESTÄTIGEN WOLLEN, GEBEN SIE \"BESTÄTIGEN\" EIN.");
            } while (!s.nextLine().equals("BESTÄTIGEN"));
            String hashedpw = hashPassword(password);
            Logger.log("client", "Sende Anmeldedaten");
            v.sendeJSON(new JSONObject(String.format("{\"type\":\"login\",\"name\":\"%s\",\"password\":%s}", benutzername, hashedpw)));
            antwort = serverInput();
            Logger.log("client", "Antwort empfangen");
            if (antwort.getString("type").equals("authresponse")) {
                if (antwort.getBoolean("success")) {
                    System.out.println("ERFOLGREICH ANGEMELDET");
                    Logger.log("client", "Anmeldung erfolgreich");
                    this.eingeloggt = true;
                    if (antwort.getLong("opengame") != -1) {
                        Logger.log("client", "Offenes Spiel gefunden");
                        System.out.println("SIE HABEN NOCH EIN OFFENES SPIEL. FALLS SIE EIN ANDERES SPIEL SPIELEN WOLLEN, MÜSSEN SIE DIESES ZUNÄCHST FERTIG SPIELEN ODER AUFGEBEN");
                        this.imSpiel = true;
                    }
                    return;
                } else {
                    Logger.log("client", "Anmeldevorgang fehlgeschlagen. Grund: " + antwort.getString("error"));
                    System.out.println("FEHLER BEIM ANMELDEN. FEHLER: " + antwort.getString("error"));
                }
            } else {
                Logger.log("client", "Fehler im Protokoll");
                System.out.println("FEHLER IM PROTOKOLL");
            }
        }
    }

    private void registrieren() {
        Logger.log("client", "Starte Registrierungsvorgang");
        Scanner s = new Scanner(System.in);
        String benutzername;
        String password;
        JSONObject antwort;
        System.out.println("VORGANG ABBRECHEN MIT: \"ABBRECHEN\"");
        while (true) {
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
            Logger.log("client", "Sende Registrierungsdaten");
            v.sendeJSON(new JSONObject(String.format("{\"type\":\"register\",\"name\":\"%s\",\"password\":%s}", benutzername, hashedpw)));
            antwort = serverInput();
            if (antwort.getString("type").equals("authresponse")) {
                if (antwort.getBoolean("success")) {
                    Logger.log("client", "Registrierung erfolgreich");
                    System.out.println("ERFOLGREICH REGISTRIERT");
                    this.eingeloggt = true;
                    return;
                } else {
                    Logger.log("client", "Registrierungsvorgang fehlgeschlagen. Grund: " + antwort.getString("error"));
                    System.out.println("FEHLER BEIM REGISTRIEREN. FEHLER: " + antwort.getString("error"));
                }
            } else {
                Logger.log("client", "Fehler im Protokoll");
                System.out.println("FEHLER IM PROTOKOLL");
            }
        }
    }

    private void abmelden() {
        System.out.println("ABMELDEN..");
        Logger.log("client", "Abmeldung gesendet");
        v.sendeJSON(new JSONObject("{\"type\":\"logout\"}"));
        JSONObject response = serverInput();
        if (response.getString("type").equals("logoutresponse")) {
            Logger.log("client", "Abmeldung erfolgreich");
            System.out.println("ABMELDUNG ERFOLGREICH");
            this.eingeloggt = false;
        } else {
            Logger.log("client", "Abmeldung fehlgeschlagen");
            System.out.println("FEHLER IM PROTOKOLL");
        }
    }

    private void rangliste() {
        // TODO
    }

    private void spielmodiAuswahl() {
        Scanner s = new Scanner(System.in);
        String input;
        int modi;
        System.out.println("VORGANG ABBRECHEN: \"ABBRECHEN\"");
        while (true) {
            System.out.println("WÄHLEN SIE EINEN SPIELMODE:");
            for (int i = 0; i < alleSpielmodi.length; i++) {
                System.out.println(i + ":\t" + alleSpielmodi[i]);
            }
            input = s.nextLine();
            if (input.equalsIgnoreCase("ABBRECHEN")) {
                System.out.println("VORGANG ABGEBROCHEN");
                return;
            }
            if (isInteger(input)) {
                modi = Integer.parseInt(input);
                if (-1 < modi && modi < alleSpielmodi.length) {
                    break;
                } else {
                    System.out.println("EINGABE KEIN GÜLTIGER SPIELMODE");
                }
            } else {
                System.out.println("EINGABE KEINE ZAHL");
            }
        }
        // mode-abhängige Daten
        if(modi == 2){ // uuid, des spiels, dem man beitreten will
            long uuid;
            while(true){
                input = s.nextLine();
                if (input.equalsIgnoreCase("ABBRECHEN")) {
                    System.out.println("VORGANG ABGEBROCHEN");
                    return;
                }
                if (isInteger(input)) {
                    uuid = Long.parseLong(input);
                    break;
                } else {
                    System.out.println("EINGABE KEINE ZAHL");
                }
            }
            v.sendeJSON(new JSONObject(String.format("{\"type\":\"modeselect\",\"mode\":%d,\"uuid\":%d}", modi, uuid)));
        } else { // andere Modi
            v.sendeJSON(new JSONObject(String.format("{\"type\":\"modeselect\",\"mode\":%d}", modi)));
        }

        JSONObject antwort = serverInput();
        if(antwort.getString("type").equals("modeconfirm")){
            if(antwort.getInt("mode") == modi){
                System.out.println("AUSWAHL ERFOLGT");
            } else {
                System.out.println("FEHLER BEI DER BESTÄTIGUNG DER AUSWAHL");
                return;
            }
        } else if (antwort.getString("type").equals("modedeny")){
            System.out.println("FEHLER BEI DER AUSWAHL. FEHLER: " + antwort.getString("error"));
            return;
        }else {
            System.out.println("FEHLER BEIM PROTOKOLL");
        }

        if(modi == 0){
            if(antwort.getBoolean("ready")){
                System.out.println("GEGNER GEFUNDEN. SPIEL STARTET");
                this.imSpiel = true;
                this.spielVorbei = false;
            } else {
                System.out.println("QUEUE BEIGETRETEN");
                queue();
            }
        } else if (modi == 1) {
            System.out.println("LOBBY ERSTELLT. UUID=" + antwort.getLong("uuid") + ". GEBEN SIE DIESE UUID EINEM FREUND, DER IHNEN DANN BEITRETEN KANN");
            this.imSpiel = true;
            this.spielVorbei = false;
        } else if (modi == 2) {
            this.imSpiel = true;
            this.spielVorbei = false;
            System.out.println("LOBBY BEIGETRETEN");
        }


    }

    private void queue() {
        System.out.println("SIE BEFINDEN SICH IN DER QUEUE. UM DIE QUEUE ZU VERLASSEN GEBEN SIE \"VERLASSEN\" EIN");
        Scanner s = new Scanner(System.in);
        String input;
        QueueNotifier qn = new QueueNotifier();
        Thread qnThread = new Thread(qn);
        qnThread.start();
        while(!v.queueReady()) {
            input = s.nextLine().toUpperCase();
            if (input.equals("VERLASSEN")) {
                v.sendeJSON(new JSONObject("{\"type\":\"leavequeue\"}"));
                qn.stoppe();
                return;
            } else if (input.equals("AKZEPTIEREN")) {
                if (v.queueReady()) {
                    JSONObject antwort = v.warteAufJSON();
                    if (antwort.getString("type").equals("queueready")) {
                        this.imSpiel = true;
                        return;
                    } else {
                        System.out.println("FEHLER IM PROTOKOLL");
                    }
                } else {
                    System.out.println("ES WURDE NOCH KEIN GEGNER GEFUNDEN");
                }
            }
        }
        qn.stoppe();
    }

    private JSONObject serverInput() {
        JSONObject res = v.warteAufJSON();
        if(res.getString("type").equals("{\"type\":\"serverclose\"}")){
            System.out.println("SERVER SCHLIEßT");
            System.exit(0);
        }
        return res;
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
        for (int i = 1; i < hashedPw.length; i++) {
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
        Client client = new Client();
        Thread clientThread = new Thread(client);
        clientThread.start();
    }

}
