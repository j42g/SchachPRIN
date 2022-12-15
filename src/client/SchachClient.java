package client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class SchachClient implements Runnable {

    private Socket server;



    @Override
    public void run() {

        Socket client = null;
        try {
            // Make connection
            System.out.println("HEHW");
            client = new Socket("localhost", 7777);

            System.out.println("Verbindung hergestellt. \nWenn Sie die Verbindung schließen wollen geben Sie \"EXIT\" ein");
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write(getArt((String[]) in.readObject()));
        } catch (Exception e) {
            System.out.println("Fehler beim Verbinden");
            e.printStackTrace();
        }

    }

    private void gameLoop() {
    }

    /**
     * Findet heraus, ob der Spieler sich zu einem anderen Spiel
     */
    private int getArt(String[] reqTypes){
        for(int i = 0; i < reqTypes.length; i++){
            System.out.println(reqTypes[i]);
        }
        System.out.println(reqTypes.length);
        Scanner s = new Scanner(System.in);
        String input;
        int temp;
        while(true)  {
            System.out.println("Wählen Sie:");
            for(int i = 0; i < reqTypes.length; i++){
                System.out.println(reqTypes[i] + "(" + i + ")");
            }

            input = s.next();
            if(!isInteger(input)){
                System.out.println("Die Eingabe war keine ganze Zahl!");
                continue;
            }
            temp = Integer.parseInt(input);
            if(!(-1 < temp && temp < reqTypes.length)){
                System.out.println("Die Zahl ist keine gültige Option!");
                continue;
            }
            return temp;
        }
    }

    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
