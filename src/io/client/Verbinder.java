package io.client;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Verbinder {

    private static String serverIP = "127.0.0.1";
    private static String serverPort = "7777";

    private static Verbinder instance = null;
    private Socket server;
    private BufferedReader in;
    private PrintWriter out;

    private Verbinder() {
        try {
            server = new Socket("127.0.0.1", 7777);
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out = new PrintWriter(server.getOutputStream());
            System.out.println("VERBINDUNG AUFGEBAUT");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("FEHLER BEIM VERBINDEN");
        }
    }

    public void trenne() {
        try {
            server.close();
            in.close();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("FEHLER BEIM BEENDEN DER VERBINDUNG");
        }
        server = null;
        in = null;
        out = null;
    }

    public static Verbinder getInstance() {
        if (instance == null) {
            instance = new Verbinder();
        }
        return instance;
    }

    public void sendeJSON(JSONObject json) {
        out.println(json.toString());
        out.flush();
    }

    public JSONObject warteAufJSON(){
        try{
            while(!in.ready()){
                Thread.sleep(100);
            }
            return new JSONObject(in.readLine());
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("FEHLER BEIM EMPFANGEN");
        }
        return new JSONObject("{\"type\":\"invalid\"}");
    }

}
