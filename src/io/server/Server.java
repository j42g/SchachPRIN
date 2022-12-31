package io.server;

import io.server.benutzerverwalktung.Benutzer;
import io.server.benutzerverwalktung.BenutzerManager;
import org.json.JSONObject;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Runnable {

    private static Server instance;

    private volatile boolean shouldRun;

    private final BenutzerManager bm = new BenutzerManager(ServerVerwaltung.filename);;
    private final ArrayList<ClientHandler> threads;
    private ClientHandler waitingClient; // random game queue
    private final ArrayList<ClientHandler> waitingPrivate;
    private final ArrayList<Game> games;

    private Server() {
        this.shouldRun = true;
        this.threads = new ArrayList<ClientHandler>();
        this.waitingClient = null;
        this.waitingPrivate = new ArrayList<ClientHandler>();
        this.games = new ArrayList<Game>();
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
        } while (!isUUIDFree(id) && id > -1);
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

    private void createGame(ClientHandler a, ClientHandler b){
        // TODO auswählen wer weiß ist
        // TODO a und b müssen irgendwie an das game kommen
    }

    public synchronized boolean einloggen(JSONObject benutzer){
        byte[] passwordArr = new byte[32];
        Iterator<Object> it = benutzer.getJSONArray("password").iterator();
        int temp;
        for (int i = 0; it.hasNext(); i++) {
            temp = (int) it.next();
            passwordArr[i] = (byte) temp;
        }
        return bm.einloggen(benutzer.getString("name"), passwordArr);
    }

    public synchronized boolean registieren(JSONObject benutzer) {
        byte[] passwordArr = new byte[32];
        Iterator<Object> it = benutzer.getJSONArray("password").iterator();
        int temp;
        for (int i = 0; it.hasNext(); i++) {
            temp = (int) it.next();
            passwordArr[i] = (byte) temp;
        }
        return bm.registieren(benutzer.getString("name"), passwordArr);
    }

    public Benutzer getNutzer(String name){
        return bm.getNutzer(name);
    }

    public synchronized boolean lookingForOpponent(ClientHandler client){
        if(waitingClient == null){
            waitingClient = client;
            return false;
        } else {
            games.add(new Game(waitingClient, client));
            return true;
        }
    }

    public synchronized boolean joinPrivate(ClientHandler client, long uuid) {
        for(int i = 0; i < waitingPrivate.size(); i++){
            if(waitingPrivate.get(i).getUUID() == uuid){
                // TODO create game with them
                games.add(new Game(waitingPrivate.get(i), client));
                waitingPrivate.remove(i);
                return true;
            }
        }
        return false;
    }

    public synchronized void waitingPrivate(ClientHandler client) {
        this.waitingPrivate.add(client);
    }

    public void stoppe() {
        this.shouldRun = false;
    }


}
