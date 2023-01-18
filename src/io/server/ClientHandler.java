package io.server;

import io.Logger;
import io.server.benutzerverwaltung.Benutzer;
import io.server.spiel.SchachSpiel;
import org.json.JSONArray;
import org.json.JSONObject;
import spiel.moves.FullMove;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClientHandler extends Thread {

    private volatile boolean shouldRun;
    private volatile boolean gegnerGefunden;

    private boolean eingeloggt;
    private boolean imSpiel;
    private boolean amZug;
    private final Server server;
    private SchachSpiel game;
    private final long UUID;
    private final Socket client;
    private Benutzer benutzer;

    private PrintWriter out;
    private BufferedReader in;


    public ClientHandler(Socket client, long UUID) {
        this.client = client;
        this.UUID = UUID;
        this.shouldRun = true;
        this.server = Server.getServer();
        this.gegnerGefunden = false;
        this.eingeloggt = false;
        this.imSpiel = false;
        this.amZug = false;
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        boolean inputErwartend = true;
        JSONObject request = null;
        String requestType = null;
        while (shouldRun) {
            if (inputErwartend) {
                try {
                    while (!in.ready() && shouldRun) {
                        Thread.sleep(10);
                    }
                    if (shouldRun) { // wenn diese Thread während dem warten gestoppt wird, darf dieser Code nicht ausgeführt werden
                        request = new JSONObject(in.readLine());
                        requestType = request.getString("type");
                        Logger.log("client-handler-" + this.UUID, "Nachricht vom Type \"" + requestType + "\" empfangen");
                        if (requestType.equals("terminate")) {
                            terminate();
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                inputErwartend = true;
            }


            if (!eingeloggt) {
                if (requestType.equals("login")) {
                    login(request);
                } else if (requestType.equals("register")) {
                    register(request);
                } else {
                    System.out.println("FEHLER IM PROTOKOLL");
                }
            } else { // eingeloggt heißt benutzer != null
                if (!imSpiel) {
                    if (requestType.equals("logout")) { // ausloggen
                        out.println("{\"type\":\"logoutresponse\"}");
                        this.benutzer = null;
                        this.eingeloggt = false;
                    } else if (requestType.equals("modeselect")) {
                        modeselect(request);
                    } else if (requestType.equals("leaderboardrequest")) {
                        leaderboard();
                    }
                } else { // im spiel
                    if (requestType.equals("forfeit")) {
                        forfeitGame();
                    }
                    if (amZug) {
                        if (requestType.equals("move")) {
                            move(request);
                        }
                    }
                }

            }
        }
    }

    private void login(JSONObject request) {
        if (!server.existiertNutzer(request)) { // benutzer existiert nicht
            out.println("{\"type\":\"authresponse\",\"success\":false,\"error\":\"ERR: BENUTZER EXISTIERT NICHT\"}");
        } else {
            this.benutzer = server.einloggen(request);
            if (this.benutzer == null) { // passwort falsch
                out.println("{\"type\":\"authresponse\",\"success\":false,\"error\":\"ERR: PASSWORT FALSCH\"}");
            } else { // alles korrekt
                if (benutzer.hatAktivesSpiel()) {
                    if (server.checkIfExists(benutzer.getUuidOffenesSpiel())) {
                        server.joinPrivate(this, benutzer.getUuidOffenesSpiel());
                    } else {
                        server.createPrivate(this, benutzer.getUuidOffenesSpiel());
                    }
                    // TODO
                } else {
                    out.println("{\"type\":\"authresponse\",\"success\":true,\"opengame\":-1}");
                }

                eingeloggt = true;
            }
        }
    }

    private void register(JSONObject request) {
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
    }

    private void modeselect(JSONObject request) {
        int gamemode = request.getInt("mode");
        switch (gamemode) {
            case 0 -> { // random game
                if (server.isQueueReady()) { // gegner verfügbar
                    out.println("{\"type\":\"modeconfirm\",\"mode\":0,\"ready\":true}");
                    starteSpiel();
                    server.queueGame(this);
                } else { // muss warten
                    out.println("{\"type\":\"modeconfirm\",\"mode\":0,\"ready\":false}");
                    starteSpiel();
                    queue(out, in);
                }
            }
            case 1 -> { // private lobby erstellt
                server.waitingPrivate(this);
                out.println(String.format("{\"type\":\"modeconfirm\",\"mode\":1,\"uuid\":%d}", this.game.getUUID()));
                starteSpiel();

            }
            case 2 -> { // privater lobby beitreten
                if (server.doesPrivateExist(request.getLong("uuid"))) {
                    out.println("{\"type\":\"modeconfirm\",\"mode\":2}");
                    server.joinPrivate(this, request.getLong("uuid"));
                    starteSpiel();
                } else {
                    out.println("{\"type\":\"modedeny\",\"error\":\"ES EXISTIERT KEIN SPIEL MIT DIESER UUID\"}");
                }
            }
        }
    }

    private void queue(PrintWriter out, BufferedReader in) {
        try {
            while (shouldRun && !gegnerGefunden) {
                if (in.ready()) {
                    JSONObject req = new JSONObject(in.readLine());
                    if (req.getString("type").equals("leavequeue")) {
                        server.removeFromQueue(this);
                        return;
                    }
                }
            }
            if (gegnerGefunden && shouldRun) {
                out.println("{\"type\":\"queueready\"}");
                server.queueGame(this);
                starteSpiel();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.log("Client-Handler-" + this.UUID, "Fehler in der Queue");
        }

    }

    private void leaderboard() {
        ArrayList<Benutzer> all = server.getAllNutzer();
        all.sort(new Comparator<Benutzer>() { // sortiere nach Elo
            @Override
            public int compare(Benutzer o1, Benutzer o2) {
                return Integer.compare(o2.getElo(), o1.getElo()); // sollte eigentlich andersrum nach java standard, aber so spart man sich das reverse
            }
        });
        JSONArray lb = new JSONArray();
        for (Benutzer benutzer : all) {
            lb.put(new JSONObject(String.format("{\"name\":\"%S\",\"elo\":%d}", benutzer.getName(), benutzer.getElo())));
        }
        JSONObject finishedLb = new JSONObject("{\"type\":\"leaderboard\"}");
        finishedLb.put("leaderboard", lb);
        out.println(finishedLb);
    }

    private void forfeitGame() {
        Logger.log("client-handler-" + this.UUID, "Gibt game " + this.game.getUUID());
        this.game.forfeit(this);
    }

    public void starteSpiel() {
        out.println(String.format("{\"type\":\"startgame\",\"fen\":\"" + game.getFen() + "\",\"color\":%d}", game.getMyColor(this)));
        this.imSpiel = true;
        this.game.start();
    }

    public void giveGame(SchachSpiel schachSpiel) {
        this.game = schachSpiel;
    }

    public void endGame(int endcode) {
        this.imSpiel = false;
        this.amZug = false;
        out.println(String.format("{\"type\":\"gameover\",\"fen\":\"" + game.getFen() + "\",\"endcode\":%d}", endcode));
        this.game = null;
    }

    public void requestMove() {
        while(!imSpiel){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
        this.amZug = true;
        out.println("{\"type\":\"moverequest\",\"fen\":\"" + game.getFen() + "\"}");
    }

    public void move(JSONObject request) {
        this.game.setMove(request.getString("move"));
        this.amZug = false;
        out.println("{\"type\":\"moveresponse\",\"success\":true}");
    }

    public long getUUID() {
        return this.UUID;
    }

    public int getElo() {
        return this.benutzer.getElo();
    }

    public void setElo(int elo) {
        this.benutzer.setElo(elo);
    }

    public SchachSpiel getSpiel() {
        return this.game;
    }

    public void gegnerGefunden() {
        this.gegnerGefunden = true;
    }

    public void terminate() { // diese funktion heißt client schließt die Verbindung
        this.shouldRun = false;
        if (imSpiel) {
            this.game.leaveGame(this);
            this.benutzer.setUuidOffenesSpiel(this.game.getUUID());
        }
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
