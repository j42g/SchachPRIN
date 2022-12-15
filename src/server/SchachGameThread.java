package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SchachGameThread implements Runnable {

    private final int UUID;
    private Socket client1;
    private Socket client2;
    private

    public SchachGameThread(Socket client1, int UUID){
        this.client1 = client1;
        this.UUID = UUID;
    }

    @Override
    public void run() {



        PrintWriter out1 = null;
        BufferedReader br1 = null;
        try {
            out1 = new PrintWriter(client1.getOutputStream(), true);
            br1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
        } catch (Exception e){
            System.out.println("Fehler beim erstellen?");
            e.printStackTrace();
            return;
        }
        out1.write("Sie sind mit nem Schachserver verbunden.\nUUID:\t" + this.id);
    }


}
