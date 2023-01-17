package io.server.spiel;

import io.Logger;
import io.server.ClientHandler;
import spiel.feld.Feld;
import spiel.feld.Quadrat;
import spiel.moves.FullMove;

public class SchachSpiel implements Runnable {

    // Elo dingens
    private static final int k = 20;

    private volatile boolean started;
    private volatile boolean shouldRun;
    private volatile int playerCount;
    private volatile String move; // muss noch zum Move Objekt gemacht werden

    private long uuid;
    private Feld feld;
    private boolean isWhiteMove;
    private ClientHandler white;
    private ClientHandler black;

    public SchachSpiel(long uuid, ClientHandler a) { // private lobby wird erstellt
        this.started = false;
        this.shouldRun = true;
        this.playerCount = 1;
        this.uuid = uuid;
        this.feld = new Feld();
        this.isWhiteMove = true;

        this.move = null;
        if (Math.random() < 0.5) { // wer ist was
            this.white = a;
            this.black = null;
        } else {
            this.white = null;
            this.black = a;
        }

        a.giveGame(this);
    }

    public SchachSpiel(long uuid, ClientHandler a, ClientHandler b) {
        this.started = false;
        this.shouldRun = true;
        this.playerCount = 2;
        this.uuid = uuid;
        this.isWhiteMove = true;
        this.feld = new Feld();
        this.move = null;
        if (Math.random() < 0.5) {
            this.white = a;
            this.black = b;
        } else {
            this.white = b;
            this.black = a;
        }
        a.giveGame(this);
        b.giveGame(this);
    }

    public SchachSpiel(String json) {
        // TODO
    }

    public synchronized boolean joinGame(ClientHandler client) {
        if (white != null && black != null) { // spiel schon voll
            return false;
        } else if (white == null && black == null) { // spieler schon geleavt oder so
            return false;
        } else if (white == null) {
            white = client;
        } else {
            black = client;
        }
        playerCount++;
        return true;
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
                    if (false) { // spielvorbei
                    }
                }
                case 1 -> {
                    if (black != null && !isWhiteMove) {
                        black.requestMove();
                        awaitMove();
                    } else if (white != null && isWhiteMove) {
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
        if (move != null) {
            //feld.move(null, null);
        }
        if (!shouldRun) {
            // TODO
        }

        this.move = null;
    }

    public synchronized void setMove(String move) {
        this.move = move;
    }

    public void move(FullMove a) { // Move Objekt

        //this.feld.move(null, null); // Moveobject
    }


    public void saveToFile() {

    }

    public long getUUID() {
        return this.uuid;
    }

    public int getMyColor(ClientHandler asker) {
        if (white.equals(asker)) {
            return 1;
        } else if (black.equals(asker)) {
            return -1;
        } else {
            return -0;
        }
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
    }


}
