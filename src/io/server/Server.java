package io.server;

import io.Logger;
import io.server.benutzerverwalktung.Benutzer;
import io.server.benutzerverwalktung.BenutzerManager;
import org.json.JSONObject;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Server implements Runnable {

    private static Server instance;

    private volatile boolean shouldRun;

    private final BenutzerManager bm = new BenutzerManager(ServerVerwaltung.filename);
    ;
    private final ArrayList<ClientHandler> threads;
    private ClientHandler waitingClient; // random game queue
    private final ArrayList<SchachSpiel> waitingPrivate;
    private final ArrayList<SchachSpiel> schachSpiels;

    private Server() {
        this.shouldRun = true;
        this.threads = new ArrayList<ClientHandler>();
        this.waitingClient = null;
        this.waitingPrivate = new ArrayList<SchachSpiel>();
        this.schachSpiels = new ArrayList<SchachSpiel>();
    }

    public static Server getServer() {
        if (instance == null) { // nicht thread-sicher
            instance = new Server();
        }
        return instance;
    }

    @Override
    public void run() {
        Socket client;
        try (ServerSocket server = new ServerSocket(7777)) {
            while (shouldRun) {
                client = server.accept();
                this.addThread(client);
            }
        } catch (Exception e) {
            System.out.println("Unbekannter Fehler.");
            e.printStackTrace();
            return;
        }
        // Soll stoppen:
        bm.abspeichern();
        for (ClientHandler sgt : this.threads) {
            sgt.stoppe();
        }
        for (ClientHandler sgt : this.threads) {
            try {
                sgt.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void addThread(Socket client1) {
        long id;
        Random gen = new Random();
        do {
            id = gen.nextLong();
        } while (!isClientUUIDFree(id) && id > -1);
        ClientHandler sgt = new ClientHandler(client1, id);
        sgt.start();
        this.threads.add(sgt);
        Logger.log("server", "Client-Handler-" + id + " gestartet");
    }

    private synchronized void speichereSpiel(String fen) {
        // TODO implementieren
        // Speichern: FEN, Spieler, welcher Spieler welche Farbe hat
        // format JSON
        // Dateiname als static field etc.
    }

    private boolean isClientUUIDFree(long id) {
        for (ClientHandler sgt : this.threads)
            if (sgt.getUUID() == id) {
                return false;
            }
        return true;
    }

    private long generateGameUUID(){
        long id;
        Random gen = new Random();
        do {
            id = gen.nextLong();
        } while (!isGameUUIDFree(id) && id > -1);
        return id;
    }

    private boolean isGameUUIDFree(long id) {
        for (SchachSpiel spiel : waitingPrivate) {
            if (spiel.getUUID() == id) {
                return false;
            }
        }
        for (SchachSpiel spiel : schachSpiels) {
            if (spiel.getUUID() == id) {
                return false;
            }
        }
        return true;
    }

    public boolean existiertNutzer(JSONObject benutzer) {
        return bm.existiertBenutzer(benutzer.getString("name"));
    }

    public synchronized Benutzer einloggen(JSONObject benutzer) {
        Logger.log("server", "Versuche " + benutzer.getString("name") + " einzuloggen");
        if (!bm.existiertBenutzer(benutzer.getString("name"))) {
            Logger.log("server", benutzer.getString("name") + " existiert nicht");
            return null;
        }
        byte[] passwordArr = new byte[32];
        Iterator<Object> it = benutzer.getJSONArray("password").iterator();
        int temp;
        for (int i = 0; it.hasNext(); i++) {
            temp = (int) it.next();
            passwordArr[i] = (byte) temp;
        }
        Benutzer nutzer = bm.einloggen(benutzer.getString("name"), passwordArr);
        if (nutzer == null) {
            Logger.log("server", benutzer.getString("name") + " existiert, Passwort falsch");
        } else {
            Logger.log("server", benutzer.getString("name") + " existiert, Passwort korrekt");
        }
        return nutzer;
    }

    public synchronized Benutzer registrieren(JSONObject benutzer) {
        Logger.log("server", "Versuche " + benutzer.getString("name") + " zu registrieren");
        if (bm.existiertBenutzer(benutzer.getString("name"))) {
            Logger.log("server", benutzer.getString("name") + " existiert schon");
            return null;
        }
        byte[] passwordArr = new byte[32];
        Iterator<Object> it = benutzer.getJSONArray("password").iterator();
        int temp;
        for (int i = 0; it.hasNext(); i++) {
            temp = (int) it.next();
            passwordArr[i] = (byte) temp;
        }
        Logger.log("server", benutzer.getString("name") + " registriert");
        return bm.registrieren(benutzer.getString("name"), passwordArr);
    }

    public synchronized boolean lookingForOpponent(ClientHandler client) {
        if (waitingClient == null) {
            Logger.log("server", client.getUUID() + "tritt Queue bei");
            waitingClient = client;
            return false;
        } else {
            Logger.log("server", client.getUUID() + "findet " + waitingClient.getUUID() + "durch Queue");
            schachSpiels.add(new SchachSpiel(generateGameUUID(), waitingClient, client));
            return true;
        }
    }

    public synchronized boolean joinPrivate(ClientHandler client, long uuid) {
        Logger.log("server", client.getUUID() + " versucht " + uuid + " beizutreten");
        for (int i = 0; i < waitingPrivate.size(); i++) {
            if (waitingPrivate.get(i).getUUID() == uuid) {
                // TODO create game with them
                schachSpiels.add(waitingPrivate.get(i));
                waitingPrivate.remove(i);
                Logger.log("server", client.getUUID() + " tritt " + uuid + " bei");
                return true;
            }
        }
        Logger.log("server", client.getUUID() + " konnte " + uuid + " nicht beitreten. " + uuid + " ist kein gültiges Spiel");
        return false;
    }

    public synchronized void waitingPrivate(ClientHandler client) {
        Logger.log("server", "Erstelle Schachlobby");

        this.waitingPrivate.add(new SchachSpiel(generateGameUUID(), client));
    }

    public void stoppe() {
        this.shouldRun = false;
        for (ClientHandler thread : threads) {
            Logger.log("server", "Stoppe Client-Handler-" + thread.getUUID());
            thread.stoppe();
        }
    }

}
