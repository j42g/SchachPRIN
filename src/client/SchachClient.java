package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SchachClient implements Runnable {

    private Socket server;
    private String[] reqTypes = new String[]{"Wollen Sie einem zufälligen Spieler spielen?",
            "Wollen Sieeinem existierenden Spiel beitreten?",
            "Wollen Sie ein Spiel erstellen?"};


    @Override
    public void run() {
        int connTyp = getArt();
        Socket client = null;
        try {
            client = new Socket("192.168.137.1", 7777);
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.write(connTyp);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Findet heraus, ob der Spieler sich zu einem anderen Spiel
     */
    private int getArt(){
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
            if(-1 < temp && temp < reqTypes.length){
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
