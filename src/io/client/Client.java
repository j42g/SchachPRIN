package io.client;

import io.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import spiel.feld.Feld;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client implements Runnable {

    private final String[] alleSpielmodi = new String[]{"RANDOM GEGNER", "PRIVATES SPIEL ERSTELLEN", "PRIVATEM SPIEL BEITRETEN"};
    private final String[] alleBefehle = new String[]{"VERBINDEN", "EXIT", "TRENNEN", "ANMELDEN", "REGISTRIEREN", "ABMELDEN", "EXIT", "SPIELMODI", "SPIELREGELN", "RANGLISTE", "ZUG", "AUFGEBEN", "VERLASSEN"};
    private final String spielRegeln = "Ziel des Spiels \nZiel eines jeden Spieles ist es, den gegnerischen König \nso anzugreifen, dass er nicht mehr verteidigt werden \nkann und somit im nächsten Zug geschlagen werden könnte.\nDiese Stellung heißt Matt. Das Ziel ist es also, den Gegner \nmattzusetzen, bevor er es tut.\n\nGrundlegende Regeln\nDer König ist die wichtigste Figur beim Schach. Ein Königs-\nangriff, auch Schach genannt, muss unverzüglich abgewehrt\nwerden. Das Spiel Schach wird auf einem Brett mit 64 Feldern\ngespielt. Ein Spieler bewegt die weißen Steine, der andere\ndie schwarzen. Es muss immer abwechselnd gezogen werden.\nWeiß beginnt. Nur gegnerische Steine können geschlagen wer-\nden. Ein geschlagener Stein ist aus dem Spiel.\n\nGangart der Figuren\n\nSpringer" + "\u2658 \n"+ "Der Springer kann wie im Bild angegeben ziehen. Im Gegen-\nsatz zu allen anderen Figuren kann er andere Steine über-\nspringen. Er zieht immer zwei Felder horizontal und ein Feld\nvertikal oder zwei Felder vertikal und ein Feld horizontal.\n\nLäufer"+"\u2657 \n"+ "Läufer ziehen diagonal beliebig weit über das Brett, wobei \nsie nicht über andere Figuren hinweg ziehen dürfen. Aufgrund\nder diagonalen Zugweise kann ein Läufer nur Felder gleicher\nFeldfarbe erreichen. Dies bedeutet eine Einschränkung seiner\nZugmöglichkeiten und damit eine Schwäche des Läufers.\n\nTurm"+ "\u2656\n"+ "Ein Turm kann sich sowohl horizontal als auch vertikal über\neine beliebige Anzahl von Feldern bewegen. Er darf auf jedes\nfreie Feld in jeder Richtung linear ziehen, ohne jedoch über\nandere Figuren zu springen. Die einzige Ausnahme davon bildet\ndie Rochade, in deren Verlauf der Turm einmalig über den König\nspringt.\n\nDame"+ "\u2655 \n"+"Die Dame darf auf jedes freie Feld derselben Linie, Reihe oder\nDiagonale ziehen, ohne jedoch über andere Figuren zu springen\nund vereint somit die Wirkung eines Turms und eines Läufers in\nsich. Damit ist die Dame die beweglichste aller Figuren.\n\nBauer"+ "\u2659 \n"+ "Der Bauer ist die einzige Figur, die nicht rückwärts ziehe\nkann. Ebenso ist der Bauer die einzige Figur, die anders\nschlägt als zieht: er schlägt immer diagonal, zieht aber\ngerade.\n\nKönig" +
            "\u2654 \n"+"Der König kann jeweils ein Feld in jede Richtung gehen.\nDamit kann er alle Felder des Schachbretts erreichen. Wegen\nseiner kleineSÜn Reichweite benötigt er dazu aber viele Züge.\nDer König darf kein bedrohtes Feld betreten.\n\nRochade\nBei einer Rochade tauschen König und Turm die Plätze. Der\nSpieler muss immer den König zuerst bewegen." +
            "Bei der kurzen-\noder auch kleinen Rochade von Weiß, zieht der König von e1\nnach g1 und der Turm von h1 nach f1. Für Schwarz entsprechend\nKönig e8-g8 + Turm h8-f8." +
            "Bei der langen- oder auch großen\nRochade von Weiß, zieht der König von e1 nach c1 und der Turm\nvon a1 nach d1. Für Schwarz entsprechend König e8-c8 + Turm\na8-d8." +
            "\nDie Rochade kann nur dann ausgeführt werden, wenn\n\n" +
            "-der König noch nicht bewegt wurde.\n\n" +
            "-der beteiligte Turm noch nicht gezogen wurde.\n\n" +
            "-zwischen dem König und dem beteiligten Turm \n keine andere Figur steht.\n\n" +
            "-der König über kein Feld ziehen muss, das durch\neine feindliche Figur bedroht wird.\n\n" +
            "-der König vor und nach Ausführung der Rochade\n nicht im Schach steht.\n\n" +
            "-Turm und König müssen auf derselben Reihe stehen.\n\nDie Wertung einer Partie\n\nDie Partie zählt als gewonnen (=ein Punkt), wenn\n\n" +
            "-der Gegner aufgibt.\n\n" +
            "-der gegnerische König mattgesetzt wurde.\n\n" +
            "Die Partie zählt als Remis (Unentschieden) (=ein halber Punkt), wenn\n\n" +
            "-das Remis im gegenseitigen Einvernehmen ausgehandelt wurde.\n\n" +
            "-ein Patt entstanden ist, das heißt, wenn der König nicht im\n Schach steht, aber aufgrund von Zugzwang jetzt ins Schach\n ziehen müsste.\n\n" +
            "-die Partie auch bei ungeschicktestem Spiel von keinem Spieler\n mehr gewonnen werden kann.\n\n" +
            "-eine Stellung zum dritten Mal entsteht, und jedes Mal derselbe\n Spieler am Zuge ist.\n\n" +
            "-50 Züge lang keine Figur geschlagen und kein Bauer gezogen\n worden ist.\n";

    private Verbinder v;
    private Feld feld;

    private boolean verbunden;
    private boolean eingeloggt;
    private boolean imSpiel;
    private volatile boolean amZug;

    public Client() {
        this.v = null;
        this.verbunden = false;
        this.eingeloggt = false;
        this.imSpiel = false;
        this.amZug = false;
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
                            System.out.println(spielRegeln);
                        } else if (input.equals("RANGLISTE")) {
                            rangliste();
                        }
                    } else { // IM SPIEL
                        if (input.equals("AUFGEBEN")) {
                            v.sendeJSON(new JSONObject("{\"type\":\"forfeit\"}"));
                        }  else if (input.equals("VERLASSEN")) {
                            // TODO
                        } else if (input.equals("ZUG")) {
                            if (amZug) {
                                ziehen();
                            } else {
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
                        verfuegbareBefehle.add("VERLASSEN");
                        verfuegbareBefehle.add("AUFGEBEN");
                        if (amZug) {
                            verfuegbareBefehle.add("[ZUG]");
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
        if (modi == 2) { // uuid, des spiels, dem man beitreten will
            System.out.println("AUSWAHL ERFOLGT");
            long uuid;
            while (true) {
                System.out.println("UUID: ");
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
        if (antwort.getString("type").equals("modeconfirm")) {
            if (antwort.getInt("mode") == modi) {
                System.out.println("AUSWAHL ERFOLGT");
            } else {
                System.out.println("FEHLER BEI DER BESTÄTIGUNG DER AUSWAHL");
                return;
            }
        } else if (antwort.getString("type").equals("modedeny")) {
            System.out.println("FEHLER BEI DER AUSWAHL. FEHLER: " + antwort.getString("error"));
            return;
        } else {
            System.out.println("FEHLER BEIM PROTOKOLL");
        }

        if (modi == 0) {
            if (antwort.getBoolean("ready")) {
                System.out.println("GEGNER GEFUNDEN. SPIEL STARTET");
                starteSpiel();
            } else {
                System.out.println("QUEUE BEIGETRETEN");
                queue();
            }
        } else if (modi == 1) {
            System.out.println("LOBBY ERSTELLT. UUID=" + antwort.getLong("uuid") + ". GEBEN SIE DIESE UUID EINEM FREUND, DER IHNEN DANN BEITRETEN KANN");
            starteSpiel();
        } else if (modi == 2) {
            starteSpiel();
            System.out.println("LOBBY BEIGETRETEN");
        }


    }

    private void rangliste() {
        v.sendeJSON(new JSONObject("{\"type\":\"leaderboardrequest\"}"));
        JSONObject res = v.warteAufJSON();
        if (res.getString("type").equals("leaderboard")) {
            JSONArray lb = res.getJSONArray("leaderboard");
            JSONObject benutzer;
            for (Object benutzerObj : lb) {
                benutzer = (JSONObject) benutzerObj;
                System.out.println(benutzer.getString("name") + ":\t" + benutzer.getLong("elo"));
            }
        } else {
            System.out.println("Fehler im Protokoll");
        }
    }

    private void queue() {
        System.out.println("SIE BEFINDEN SICH IN DER QUEUE. UM DIE QUEUE ZU VERLASSEN GEBEN SIE \"VERLASSEN\" EIN");
        Scanner s = new Scanner(System.in);
        String input;
        QueueNotifier qn = new QueueNotifier();
        Thread qnThread = new Thread(qn);
        qnThread.start();
        while (!v.queueReady()) {
            input = s.nextLine().toUpperCase();
            if (input.equals("VERLASSEN")) {
                v.sendeJSON(new JSONObject("{\"type\":\"leavequeue\"}"));
                qn.stoppe();
                return;
            } else if (input.equals("AKZEPTIEREN")) {
                if (v.queueReady()) {
                    JSONObject antwort = v.warteAufJSON();
                    if (antwort.getString("type").equals("queueready")) {
                        starteSpiel();
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

    private void starteSpiel() {
        this.imSpiel = true;
        JSONObject fen = v.warteAufJSON();
        if (fen.getString("type").equals("fen")) {
            this.feld = new Feld(fen.getString("fen"));
        } else {
            Logger.log("Client", "Messagetype ist nicht fen. Fehler im Protokoll");
            System.out.println("Fehler im Protokoll");
        }
        MoveListener ml = new MoveListener(this, v);
        Thread mlThread = new Thread(ml);
        mlThread.start();
    }

    public void amZug() {
        this.amZug = true;
        JSONObject fen = v.warteAufJSON();
        if (fen.getString("type").equals("moverequest")) {
            System.out.println("SIE SIND AM ZUG. GEBEN SIE \"ZUG\"");
            this.feld = new Feld(fen.getString("fen"));
        } else {
            System.out.println("Unbekannter Fehler");
        }
    }

    private void ziehen() {
        Logger.log("client", "Starte Zuginput");
        Scanner s = new Scanner(System.in);
        String move;
        while (true) {
            System.out.println("Geben sie einen Zug ein (long algebraic notation): ");
            move = s.nextLine();
            boolean isValid = false;
            // TODO feld.checkIfValid(Move.parseMove(input))
            if (isValid) {
                // TODO feld.move(Move.parseMove(input))
                break;
            } else {
                System.out.println("Zugformat inkorrekt. (Ursprungsfeld)(Zielfeld)(opt. Promotionsfigur). Bsp: e2e4, f7f8q");
            }
        }
        v.sendeJSON(new JSONObject("{\"type\":\"move\",\"move\":\"" + move + "\"}"));
        JSONObject res = v.warteAufJSON();
        if (res.getString("type").equals("moveresponse")) {
            if (res.getBoolean("success")) {
                amZug = false;
            } else {
                System.out.println("Unbekannter Fehler");
            }
        } else {
            System.out.println("Fehler im Protokoll");
        }

    }

    private JSONObject serverInput() {
        JSONObject res = v.warteAufJSON();
        if (res.getString("type").equals("{\"type\":\"serverclose\"}")) {
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
