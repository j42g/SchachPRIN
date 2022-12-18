package io.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Iterator;

public class ClientHandler extends Thread implements Serializable{

    private static final String spielOptionenMSG = """
            {"type":"text","text":"Wollen Sie einem zufälligen Spieler spielen? (0) Wollen Sie einem existierenden Spiel beitreten? (1) Wollen Sie ein Spiel erstellen? (2)]}
            """;
    /** Login/Registrierung
     * {"type":"authresponse","success":true}
     * {"type":"authresponse","success":false}
     */
    private volatile boolean shouldRun;


    private int state;
    private final long UUID;
    private final Socket client;


    public ClientHandler(Socket client, long UUID) {
        this.client = client;
        this.UUID = UUID;
        this.state = 0;
        this.shouldRun = true;
    }

    @Override
    public void run() {
        int state = 0;
        JSONObject request;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream())){
            while (shouldRun) {
                switch (state){
                    case 0 -> {
                        request = new JSONObject(in.readLine());
                        if(request.get("type").equals("login")){
                            if(Server.einloggen(request)){
                                out.println("{\"type\":\"authresponse\",\"success\":true}");
                            } else {
                                out.println("{\"type\":\"authresponse\",\"success\":false}");
                            }
                        } else if (request.get("type").equals("register")){
                            if(Server.registieren(request)){
                                out.println("{\"type\":\"authresponse\",\"success\":true}");
                            } else {
                                out.println("{\"type\":\"authresponse\",\"success\":false}");
                            }
                        }
                        out.flush();
                    }
                }
                if(!in.ready()){ // waits till input
                    Thread.sleep(10);
                }
                /*switch (state) {
                    case 0 -> {

                    }
                }*/
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void codeSpeicher() {
        PrintWriter out1 = null;
        BufferedReader br1 = null;
        try {
            out1 = new PrintWriter(client.getOutputStream(), true);
            br1 = new BufferedReader(new InputStreamReader(client.getInputStream()));
            ObjectOutputStream raus = new ObjectOutputStream(client.getOutputStream());
            //raus.writeObject(reqTypes);
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
