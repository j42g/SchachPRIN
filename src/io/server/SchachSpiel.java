package io.server;

public class SchachSpiel implements Runnable {

    private volatile boolean shouldRun;
    private volatile int playerCount;

    private long uuid;
    private ClientHandler white;
    private ClientHandler black;
    private boolean isWhiteMove;

    public SchachSpiel(long uuid, ClientHandler a) { // private lobby wird erstellt
        this.shouldRun = true;
        this.uuid = uuid;
        this.isWhiteMove = true;
        this.playerCount = 1;
        if (Math.random() < 0.5) { // wer ist was
            this.white = a;
            this.black = null;
        } else {
            this.white = null;
            this.black = a;
        }
        // threads informieren
        a.giveGame(this);
        // TODO feld generieren
        // TODO weiß nach move fragen aka gameloop starten
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    public SchachSpiel(long uuid, ClientHandler a, ClientHandler b) {
        this.uuid = uuid;
        this.isWhiteMove = true;
        this.playerCount = 2;
        if (Math.random() < 0.5) {
            this.white = a;
            this.black = b;
        } else {
            this.white = b;
            this.black = a;
        }
        // threads informieren
        a.giveGame(this);
        b.giveGame(this);
        // TODO feld generieren
        // TODO weiß nach move fragen aka gameloop starten
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    public SchachSpiel(String json) {

    }

    public synchronized boolean joinGame(ClientHandler client) {
        if(white != null && black != null){ // spiel schon voll
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

    public synchronized void leaveGame(ClientHandler client) {
        if(client.equals(white)) {
            white = null;
            playerCount--;
        } else if (client.equals(black)) {
            black = null;
            playerCount--;
        }
    }

    @Override
    public void run() {
        while (shouldRun) {
            switch (playerCount) {
                case 0 -> {
                    if(false) { // spielvorbei
                        // spiel abspeichern
                    }
                }
                case 1 -> {
                    if(black == null && !isWhiteMove) {
                        // schwarz kann ziehen
                    } else if (white != null && isWhiteMove)  {
                        // weiß kann ziehen
                    }
                }
                case 2 -> {
                    if (isWhiteMove) {
                        white.requestMove();
                    } else {
                        black.requestMove();
                    }
                }
            }
        }
    }

    public void move() {

    }


    public void saveToFile() {

    }

    public long getUUID() {
        return this.uuid;
    }

}
