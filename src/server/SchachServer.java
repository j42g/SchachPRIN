package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Random;

public class SchachServer implements Runnable {

    private final ArrayList<Integer> UUIDs;
    private final ArrayList<Thread> threads;

    public SchachServer(){
        this.UUIDs = new ArrayList<Integer>();
        this.threads = new ArrayList<Thread>();
    }

    @Override
    public void run(){
        ServerSocket server = null;
        Socket client;
        try{
            server = new ServerSocket(7777);
            System.out.println("Server gestartet!");
            while(true) {
                try {
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

    }


    private void addThread(Socket client1){
        int id = 0;
        Random gen = new Random();
        while(true){
            id = gen.nextInt();
            if(!this.UUIDs.contains(id)){
                break;
            }
        }
        Thread a = new Thread(new SchachGameThread(client1, id));
        a.start();
        this.threads.add(a);
        this.UUIDs.add(id);
    }


}
