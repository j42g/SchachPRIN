package io.server;

import io.Logger;
import io.server.benutzerverwalktung.Benutzer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private volatile boolean shouldRun;
    private volatile boolean gegnerGefunden;

    private boolean eingeloggt;
    private boolean imSpiel;
    private boolean amZug;
    private boolean spielVorbei;
    private final Server server;
    private SchachSpiel game;
    private final long UUID;
    private final Socket client;
    private Benutzer benutzer;


    public ClientHandler(Socket client, long UUID) {
        this.client = client;
        this.UUID = UUID;
        this.shouldRun = true;
        this.server = Server.getServer();
        this.gegnerGefunden = false;
        this.eingeloggt = false;
        this.imSpiel = false;
        this.amZug = false;
        this.spielVorbei = false;

    }

    @Override
    public void run() {
        boolean inputErwartend = true;
        JSONObject request = null;
        String requestType = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
            while (shouldRun) {
                if (inputErwartend) {
                    while (!in.ready() && shouldRun) {
                        Thread.sleep(10);
                    }
                    if(shouldRun) { // wenn diese Thread während dem warten gestoppt wird, darf dieser Code nicht ausgeführt werden
                        request = new JSONObject(in.readLine());
                        requestType = request.getString("type");
                        Logger.log("client-handler-" + this.UUID, "Nachricht vom Type \"" + requestType + "\" empfangen");
                        if (requestType.equals("terminate")) {
                            terminate();
                        }
                    }
                } else {
                    inputErwartend = true;
                }
                if (!eingeloggt) {
                    if (requestType.equals("login")) {
                        if (!server.existiertNutzer(request)) { // benutzer existiert nicht
                            out.println("{\"type\":\"authresponse\",\"success\":false,\"error\":\"ERR: BENUTZER EXISTIERT NICHT\"}");
                        } else {
                            this.benutzer = server.einloggen(request);
                            if (this.benutzer == null) { // passwort falsch
                                out.println("{\"type\":\"authresponse\",\"success\":false,\"error\":\"ERR: PASSWORT FALSCH\"}");
                            } else { // alles korrekt
                                out.println(String.format("{\"type\":\"authresponse\",\"success\":true,\"opengame\":%d}", benutzer.getUuidOffenesSpiel()));
                                eingeloggt = true;
                            }
                        }
                    } else if (requestType.equals("register")) {
                        if (server.existiertNutzer(request)) { // benutzer existiert nicht schon
                            out.println("{\"type\":\"authresponse\",\"success\":false,\"error\":\"ERR: BENUTZER EXISTIERT SCHON\"}");
                        } else {
                            this.benutzer = server.registrieren(request);
                            if (this.benutzer == null) { // gute Frage wie man hier hinkommt
                                out.println("{\"type\":\"authresponse\",\"success\":false,\"error\":\"ERR: UNBEKANNT\"}");
                            } else { // alles korrekt
                                out.println("{\"type\":\"authresponse\",\"success\":true}");
                                eingeloggt = true;
                            }
                        }
                    } else {
                        System.out.println("FEHLER IM PROTOKOLL");
                    }
                } else { // eingeloggt heißt benutzer != null (hoffentlich)
                    if (!imSpiel) {
                        if (requestType.equals("logout")) {
                            out.println("{\"type\":\"logoutresponse\"}");
                            this.benutzer = null;
                            this.eingeloggt = false;
                        } else if (requestType.equals("modeselect")) {
                            int gamemode = request.getInt("mode");
                            switch (gamemode) {
                                case 0 -> { // random game
                                    if(server.lookingForOpponent(this)){ // gegner verfügbar
                                        out.println("{\"type:\"modeconfirm\",\"mode\":0,\"ready\":true}");
                                        this.imSpiel = true;
                                    } else { // muss warten
                                        out.println("{\"type:\"modeconfirm\",\"mode\":0,\"ready\":false}");
                                        queue(out, in);
                                    }
                                }
                                case 1 -> { // private lobby erstellt
                                    server.waitingPrivate(this);
                                    out.println(String.format("{\"type:\"modeconfirm\",\"mode\":1,\"uuid\":%d}", this.game.getUUID()));
                                    this.imSpiel = true;
                                }
                                case 2 -> { // privater lobby beitreten
                                    if(server.joinPrivate(this, request.getLong("uuid"))){
                                        out.println("{\"type:\"modeconfirm\",\"mode\":1");
                                    } else {
                                        out.println("{\"type:\"modedeny\",\"error\":\"ES EXISTIERT KEIN SPIEL MIT DIESER UUID\"}");
                                    }
                                }
                            }
                        }
                    } else { // im spiel
                        leaveGame();
                    }

                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("FEHLER BEI DER VERBINDUNG");
        }
    }

    public void queue(PrintWriter out, BufferedReader in) {
        try {
            while(shouldRun && !gegnerGefunden){
                if(in.ready()) {
                    JSONObject req = new JSONObject(in.readLine());
                    if (req.getString("type").equals("leavequeue")) {
                        return;
                    }
                }
            }
            if(gegnerGefunden && shouldRun){
                out.println("{\"type\":\"queueready\"}");

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.log("Client-Handler-" + this.UUID, "Fehler in der Queue");
        }

    }

    public void leaveGame() {
        Logger.log("client-handler-" + this.UUID, "Verlässt game " + this.game.getUUID());
        this.game.leaveGame(this);
    }

    public void giveGame(SchachSpiel schachSpiel) {
        this.game = schachSpiel;
        this.imSpiel = true;
    }

    public void requestMove() {
        this.amZug = true;
    }

    public long getUUID() {
        return this.UUID;
    }

    public SchachSpiel getSpiel() {
        return this.game;
    }

    public void gegnerGefunden() {
        this.gegnerGefunden = true;
    }

    public void terminate() { // diese funktion heißt client schließt die Verbindung
        this.shouldRun = false;
        // TODO implementieren
    }

    public void stoppe() { // diese funktion ist eine funktion, die nur vom Server aufgerufen werden soll, wenn dieser gestoppt wird
        this.shouldRun = false;
        // TODO mehr stuff handel
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ClientHandler e) {
            return this.UUID == e.UUID;
        }
        return false;
    }

}
