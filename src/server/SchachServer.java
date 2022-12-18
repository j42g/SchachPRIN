package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class SchachServer implements Runnable {

    private static SchachServer instance;

    private volatile boolean shouldRun;

    private final BenutzerManager bm;
    private final ArrayList<SchachGameThread> threads;

    private SchachServer(){
        this.shouldRun = true;
        this.bm = new BenutzerManager(SchachServerVerwaltung.filename);
        this.threads = new ArrayList<SchachGameThread>();
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
        } while (isUUIDFree(id));
        SchachGameThread sgt = new SchachGameThread(client1, id);
        sgt.start();
        this.threads.add(sgt);
    }

    private boolean isUUIDFree(long id){
        for(SchachGameThread sgt : this.threads)
            if(sgt.getUUID() == id){
                return false;
            }
        return true;
    }

    public void stoppe(){
        for(SchachGameThread sgt : this.threads){
            sgt.stoppe();
        }
        for(SchachGameThread sgt : this.threads){
            try {
                sgt.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.shouldRun = false;
    }
}
