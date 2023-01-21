package io.server.spiel;

import io.Logger;
import io.server.ClientHandler;
import io.server.Server;
import org.json.JSONObject;
import spiel.feld.Feld;
import spiel.feld.Quadrat;
import spiel.moves.FullMove;

public class SchachSpiel implements Runnable {


    private final Server server;

    // Elo dingens
    private static final int k = 20;

    private volatile boolean started;
    private volatile boolean shouldRun;
    private volatile int playerCount;
    private volatile String move; // muss noch zum Move Objekt gemacht werden

    private final long uuid;
    private final Feld feld;
    private boolean isWhiteMove;
    private ClientHandler white;
    private String whiteName;
    private ClientHandler black;
    private String blackName;

    public SchachSpiel(long uuid, ClientHandler a) { // private lobby wird erstellt
        this.server = Server.getServer();

        this.started = false;
        this.shouldRun = true;
        this.playerCount = 1;
        this.uuid = uuid;
        this.feld = new Feld();
        this.isWhiteMove = true;
        this.move = null;

        if (Math.random() < 0.5) { // wer ist was
            this.black = null;
            this.white = a;
            this.whiteName = a.getBenutzerName();
        } else {
            this.white = null;
            this.black = a;
            this.blackName = a.getBenutzerName();
        }
        a.giveGame(this);
    }

    public SchachSpiel(long uuid, ClientHandler a, ClientHandler b) {
        this.server = Server.getServer();

        this.started = false;
        this.shouldRun = true;
        this.playerCount = 2;
        this.uuid = uuid;
        this.feld = new Feld();
        this.isWhiteMove = true;
        this.move = null;

        if (Math.random() < 0.5) {
            this.white = a;
            this.whiteName = a.getBenutzerName();
            this.black = b;
            this.blackName = b.getBenutzerName();
        } else {
            this.white = b;
            this.whiteName = b.getBenutzerName();
            this.black = a;
            this.blackName = a.getBenutzerName();
        }
        a.giveGame(this);
        b.giveGame(this);
    }

    public SchachSpiel(long uuid, ClientHandler a, Spiel spiel) {
        this.server = Server.getServer();

        this.started = false;
        this.shouldRun = true;
        this.playerCount = 1;
        this.uuid = uuid;
        this.feld = new Feld(spiel.getFen());
        this.isWhiteMove = feld.playerTurn == 1;
        this.move = null;
        if (spiel.getWhite().equals(a.getBenutzerName())) {
            this.white = a;
        } else if (spiel.getBlack().equals(a.getBenutzerName())) {
            this.black = a;
        } else {
            System.out.println("Fehler beim Laden des Spiels. Nutzer ist weder weiß, noch schwarz");
        }
        a.giveGame(this);
    }

    public synchronized void forfeit(ClientHandler client) {
        if (client.equals(white)) {
            endGame(-1);
        } else if (client.equals(black)) {
            endGame(1);
        }
    }

    public synchronized void start() {
        if (this.started) {
            return;
        } else {
            this.started = true;
            (new Thread(this)).start();
        }
    }

    @Override
    public void run() {
        while (shouldRun) {
            switch (playerCount) {
                case 0 -> {
                    if (!feld.isDrawn() && feld.isWon() == 0) { // spiel nicht vorbei
                        server.speichereSpiel(whiteName, blackName, uuid, feld.toFen());
                    } else {
                        return;
                    }
                }
                case 1 -> {
                    if (black != null && !isWhiteMove) {
                        Logger.log("SchachSpiel-" + uuid, "Frage Schwarz nach Zug");
                        black.requestMove();
                        awaitMove();
                    } else if (white != null && isWhiteMove) {
                        Logger.log("SchachSpiel-" + uuid, "Frage Weiß nach Zug");
                        white.requestMove();
                        awaitMove();
                    } else {

                    }
                }
                case 2 -> {
                    if (isWhiteMove) {
                        white.requestMove();
                    } else {
                        black.requestMove();
                    }
                    awaitMove();
                }
            }
        }
    }

    public synchronized boolean joinGame(ClientHandler client) {
        if (white != null && black != null) { // spiel schon voll
            return false;
        } else if (white == null && black == null) { // spieler schon geleavt oder so
            return false;
        } else if (white == null) {
            white = client;
            whiteName = client.getBenutzerName();
            client.giveGame(this);
        } else {
            black = client;
            blackName = client.getBenutzerName();
            client.giveGame(this);
        }
        playerCount++;
        return true;
    }

    public synchronized void leaveGame(ClientHandler client) {
        if(client.equals(white) || client.equals(black)){
            playerCount--;
            if (white.equals(client)) {
                white = null;
            } else {
                black = null;
            }
        } else {
            Logger.log("SchachSpiel-" + this.uuid, "Client-Handle-" + client.getUUID() + " ist nicht in diesem Spiel");
        }
    }

    public void awaitMove() {
        while (shouldRun && move == null) {
            try {
                Thread.sleep(10);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (!shouldRun) {
            // TODO
        }
        if (move != null) {
            feld.move(feld.parseMove(move));
            if (feld.isDrawn()) {
                endGame(0);
            } else if (feld.isWon() == Feld.WEISS) {
                endGame(1);
            } else if (feld.isWon() == Feld.SCHWARZ) {
                endGame(-1);
            }
            move = null;
            isWhiteMove = !isWhiteMove;
        }
        this.move = null;
    }


    public synchronized void setMove(String move) {
        this.move = move;
    }

    public void saveToFile() {

    }

    public long getUUID() {
        return this.uuid;
    }

    public int getMyColor(ClientHandler asker) {
        if (white != null) {
            if (white.equals(asker)) {
                return 1;
            }
        }
        if (black != null) {
            if (black.equals(asker)) {
                return -1;
            }
        }
        return 0;
    }

    public String getFen() {
        return this.feld.toFen();
    }

    private void endGame(int endCode) { // -1 Schwarz gewonnen, 0 Unentschieden, 1 Weiss gewonnen
        // --------------------- Elo ---------------------
        double weissPunkte = (endCode + 1d) / 2d;
        double ratingWeiss = white.getElo();
        double ratingSchwarz = black.getElo();
        double erwartungWeiss = 1 / (1 + Math.pow(10, (ratingSchwarz - ratingWeiss) / 400d));
        double erwartungSchwarz = 1 - erwartungWeiss;
        int neuRatingWeiss = (int) Math.round(ratingWeiss + k * (weissPunkte - erwartungWeiss));
        int neuRatingSchwarz = (int) Math.round(ratingSchwarz + k * ((1 - weissPunkte) - erwartungSchwarz));
        white.setElo(neuRatingWeiss);
        black.setElo(neuRatingSchwarz);
        // TODO noch mehr stuff wahrscheinlich
        // ------------------- Clients ------------------
        shouldRun = false;
        if (white != null) {
            white.endGame(endCode);
        }
        if (black != null) {
            black.endGame(endCode);
        }
    }


}
