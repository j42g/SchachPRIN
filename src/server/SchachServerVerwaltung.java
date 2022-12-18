package server;

import java.util.Scanner;

public class SchachServerVerwaltung implements Runnable {

    public static final String filename = "nutzer.txt";

    private boolean aktiv;

    public SchachServerVerwaltung(){
        this.aktiv = false;
    }

    @Override
    public void run(){
        Scanner s = new Scanner(System.in);
        String input;
        System.out.println("STARTE PROGRAMM. BEFEHLE: \"START\", \"STOP\" und \"EXIT\"");
        while(true){
            input = s.nextLine();
            switch (input) {
                case "START" -> {
                    if (this.aktiv) {
                        System.out.println("SERVER IS BEREITS AKTIV");
                    } else {
                        System.out.println("STARTE SERVER");
                        starte();
                    }
                }
                case "STOP" -> {
                    if (!this.aktiv) {
                        System.out.println("SERVER IS BEREITS GESTOPPT");
                    } else {
                        System.out.println("STOPPE SERVER");
                        stoppe();
                    }
                }
                case "EXIT" -> {
                    if (this.aktiv) {
                        System.out.println("SERVER MUSS ERST GESTOPPT WERDEN");
                    } else {
                        System.out.println("BEENDE PROGRAMM");
                        return;
                    }
                }
                default -> System.out.println("UNBEKANNTER BEFEHL");
            }
        }
    }

    private void stoppe() {
        this.aktiv = false;
        SchachServer server = SchachServer.getSchachServer();
        server.stoppe();
    }

    private void starte() {
        SchachServer server = SchachServer.getSchachServer();
        Thread serverThread = new Thread(server);
        serverThread.start();
        this.aktiv = true;

    }



    public static void keineLustAllesInMainZuSchreibein(){
        SchachServerVerwaltung s = new SchachServerVerwaltung();
        Thread sThread = new Thread(s);
        sThread.start();
    }

}
