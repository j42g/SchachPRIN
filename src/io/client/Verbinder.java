package io.client;

import io.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Verbinder {

    private static final String serverIP = "127.0.0.1";
    private static final int serverPort = 7777;

    private static Verbinder instance = null;
    private Socket server;
    private BufferedReader in;
    private PrintWriter out;

    private Verbinder() {
        try {
            server = new Socket(serverIP, serverPort);
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out = new PrintWriter(server.getOutputStream(), true);
            System.out.println("VERBINDUNG AUFGEBAUT");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("FEHLER BEIM VERBINDEN. STARTEN SIE DEN CLIENT NEU");
        }
    }

    public void trenne() {
        try {
            out.println("{\"type\":\"terminate\"}");
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
    }

    public JSONObject warteAufJSON(){
        try{
            while(!in.ready()){
                Thread.sleep(100);
            }
            JSONObject ans = new JSONObject(in.readLine());
            Logger.log("verbinder", "Nachricht vom Type \"" + ans.getString("type") + "\". Ganze Nachricht:" + ans);
            return ans;
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("FEHLER BEIM EMPFANGEN");
        }
        return new JSONObject("{\"type\":\"invalid\"}");
    }

    public boolean queueReady() {
        try {
            return in.ready();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Fehler in der Queue");
            return false;
        }
    }

    public boolean hasMove() {
        try {
            return in.ready();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Fehler beim Zug warten");
            return false;
        }
    }

}
