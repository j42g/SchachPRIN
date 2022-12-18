package io.server;

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

    private static final BenutzerManager bm = new BenutzerManager(ServerVerwaltung.filename);;
    private final ArrayList<ClientHandler> threads;

    private Server() {
        this.shouldRun = true;
        this.threads = new ArrayList<ClientHandler>();
    }

    public static Server getSchachServer() {
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
        } while (!isUUIDFree(id));
        ClientHandler sgt = new ClientHandler(client1, id);
        sgt.start();
        this.threads.add(sgt);
    }

    private synchronized void speichereSpiel(String fen){
        // TODO implementieren
        // Speichern: FEN, Spieler, welcher Spieler welche Farbe hat
        // format JSON
        // Dateiname als static field etc.
    }

    private boolean isUUIDFree(long id) {
        for (ClientHandler sgt : this.threads)
            if (sgt.getUUID() == id) {
                return false;
            }
        return true;
    }

    public static synchronized boolean einloggen(JSONObject benutzer){
        byte[] passwordArr = new byte[32];
        Iterator<Object> it = benutzer.getJSONArray("password").iterator();
        int temp;
        for (int i = 0; it.hasNext(); i++) {
            temp = (int) it.next();
            passwordArr[i] = (byte) temp;
        }
        return bm.einloggen(benutzer.getString("name"), passwordArr);
    }

    public static synchronized boolean registieren(JSONObject benutzer) {
        byte[] passwordArr = new byte[32];
        Iterator<Object> it = benutzer.getJSONArray("password").iterator();
        int temp;
        for (int i = 0; it.hasNext(); i++) {
            temp = (int) it.next();
            passwordArr[i] = (byte) temp;
        }
        return bm.registieren(benutzer.getString("name"), passwordArr);
    }

    public void stoppe() {
        this.shouldRun = false;
    }
}
