package io.server;

public class SchachSpiel implements Runnable {

    private volatile boolean shouldRun;

    private long uuid;
    private ClientHandler white;
    private ClientHandler black;
    private boolean alleine;

    public SchachSpiel(long uuid, ClientHandler a) { // private lobby wird erstellt
        this.shouldRun = true;
        this.uuid = uuid;
        this.alleine = true;
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
        this.alleine = false;
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

    @Override
    public void run() {
        while (shouldRun) {
            if () {

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
