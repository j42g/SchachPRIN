package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SchachGameThread extends Thread {

    private static final String  loginMSG = """
            {"type": "loginrequest"}
            """;
    private static final String spielOptionenMSG = """
            {"type": "text","text": "Wollen Sie einem zufälligen Spieler spielen? (0) Wollen Sie einem existierenden Spiel beitreten? (1) Wollen Sie ein Spiel erstellen? (2)]}
            """;
    private volatile boolean shouldRun;

    private final long UUID;
    private Socket client1;
    private Socket client2;


    public SchachGameThread(Socket client1, long UUID) {
        this.client1 = client1;
        this.UUID = UUID;
        this.shouldRun = true;
    }

    @Override
    public void run() {
        try {
            while (shouldRun) {

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void codeSpeicher() {
        PrintWriter out1 = null;
        BufferedReader br1 = null;
        try {
            out1 = new PrintWriter(client1.getOutputStream(), true);
            br1 = new BufferedReader(new InputStreamReader(client1.getInputStream()));
            ObjectOutputStream raus = new ObjectOutputStream(client1.getOutputStream());
            raus.writeObject(reqTypes);
            int code;
            while ((code = Integer.parseInt(br1.readLine())) == 0) {

            }
            System.out.println();

        } catch (Exception e) {
            System.out.println("Fehler beim erstellen?");
            e.printStackTrace();
            return;
        }

        try {
            int mode = Integer.parseInt(br1.readLine());
            if (mode == 0) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stoppe() {
        this.shouldRun = false;
    }

    public long getUUID() {
        return this.UUID;
    }

}
