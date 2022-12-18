package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class SchachServer implements Runnable {

    private static SchachServer instance;

    private volatile boolean shouldRun;

    private final BenutzerManager bm;
    private final ArrayList<Long> UUIDs;
    private final ArrayList<Thread> threads;

    private SchachServer(){
        this.shouldRun = true;
        this.bm = new BenutzerManager(SchachServerVerwaltung.filename);
        this.UUIDs = new ArrayList<Long>();
        this.threads = new ArrayList<Thread>();
    }

    public static SchachServer getSchachServer() {
        if (instance == null) { // nicht thread-sicher
            instance = new SchachServer();
        }
        return instance;
    }

    @Override
    public void run() {
        Socket client;
        try{
            while(shouldRun) {
                try (ServerSocket server = new ServerSocket(7777)) {
                    client = server.accept();
                    this.addThread(client);
                } catch (Exception e) {
                    System.out.println("Unbekannter Fehler.");
                    e.printStackTrace();
                }
            }

        } catch(Exception e){
            System.out.println("Server konnte nicht erstellt werden.");
            e.printStackTrace();
        }
        bm.abspeichern();
        // TODO alle Games abspeichern, allen nutzern irgendwas sagen idk
    }

    private void addThread(Socket client1){
        long id;
        Random gen = new Random();
        do {
            id = gen.nextLong();
        } while (this.UUIDs.contains(id));
        Thread a = new Thread(new SchachGameThread(client1, id));
        a.start();
        this.threads.add(a);
        this.UUIDs.add(id);
    }

    public void stoppe(){
        this.shouldRun = false;
    }
}
