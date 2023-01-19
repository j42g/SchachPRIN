package io.server;

import io.Logger;

import java.util.Scanner;

public class ServerVerwaltung implements Runnable {

    public static final String filenameBenutzer = "nutzer.txt";
    public static final String filenameSpiele = "spiele.txt";

    private boolean aktiv;

    public ServerVerwaltung() {
        this.aktiv = false;
    }

    @Override
    public void run() {
        Scanner s = new Scanner(System.in);
        String input;
        System.out.println("STARTE PROGRAMM. BEFEHLE: \"START\", \"STOP\" und \"EXIT\"");
        Logger.log("server-verwaltung", "Server-Thread gestartet");
        while (true) {
            input = s.nextLine().toUpperCase();
            switch (input) {
                case "START" -> {
                    if (this.aktiv) {
                        System.out.println("SERVER IS BEREITS AKTIV");
                    } else {
                        System.out.println("STARTE SERVER...");
                        starte();
                    }
                }
                case "STOP" -> {
                    if (!this.aktiv) {
                        System.out.println("SERVER IS BEREITS GESTOPPT");
                    } else {
                        System.out.println("STOPPE SERVER...");
                        stoppe();
                    }
                }
                case "EXIT" -> {
                    if (this.aktiv) {
                        System.out.println("SERVER MUSS ERST GESTOPPT WERDEN");
                    } else {
                        System.out.println("BEENDE PROGRAMM");
                        Logger.log("server-verwaltung", "Server-Thread beendet");
                        return;
                    }
                }
                default -> System.out.println("UNBEKANNTER BEFEHL");
            }
        }
    }

    private void stoppe() {
        this.aktiv = false;
        Server server = Server.getServer();
        server.stoppe();
        System.out.println("SERVER GESTOPPT");
        Logger.log("server-verwaltung", "Server gestoppt");
    }

    private void starte() {
        Server server = Server.getServer();
        Thread serverThread = new Thread(server);
        serverThread.start();
        this.aktiv = true;
        System.out.println("SERVER GESTARTET");
        Logger.log("server-verwaltung", "Server gestartet");
    }

    public static void main(String[] args) {
        ServerVerwaltung s = new ServerVerwaltung();
        Thread sThread = new Thread(s);
        sThread.start();
    }

}
