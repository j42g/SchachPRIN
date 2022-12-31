package io.server;

import io.server.benutzerverwalktung.Benutzer;
import io.server.benutzerverwalktung.BenutzerManager;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {

    private static final String spielOptionenMSG = """
            {"type":"text","max:3","options":["Wollen Sie mit einem zufälligen Spieler spielen?", "Wollen Sie einem existierenden Spiel beitreten?", "Wollen Sie ein Spiel erstellen?"]}
            """;
    private volatile boolean shouldRun;


    private int state;
    private final Server server;
    private Game game;
    private final long UUID;
    private final Socket client;
    private Benutzer benutzer;
    private volatile boolean gegnerGefunden;
    private volatile boolean dran;


    public ClientHandler(Socket client, long UUID) {
        this.client = client;
        this.UUID = UUID;
        this.state = 0;
        this.shouldRun = true;
        this.server = Server.getServer();
        this.gegnerGefunden = false;

    }

    @Override
    public void run() {
        int state = 0;
        int subState = 0;
        boolean shouldWait = true;
        JSONObject request = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream())){
            while (shouldRun) {
                if(shouldWait){
                    while(!in.ready()){ // auf input warten
                        Thread.sleep(100);
                    }
                    request = new JSONObject(in.readLine());
                    System.out.println(request);
                    if(request.get("type").equals("terminate")){
                        // TODO HANDLE THIS
                    }
                } else {
                    shouldWait = true;
                    request = null;
                }
                switch (state){
                    case 0 -> { // auth
                        if(request.get("type").equals("login")){
                            if(server.einloggen(request)){
                                out.println("{\"type\":\"authresponse\",\"success\":true}");
                                this.benutzer = server.getNutzer(request.getString("name"));
                                state = 1;
                                shouldWait = false;
                            } else {
                                out.println("{\"type\":\"authresponse\",\"success\":false}");
                            }
                        } else if (request.get("type").equals("register")){
                            if(server.registieren(request)){
                                out.println("{\"type\":\"authresponse\",\"success\":true}");
                                this.benutzer = server.getNutzer(request.getString("name"));
                                state = 1;
                                shouldWait = false;
                            } else {
                                out.println("{\"type\":\"authresponse\",\"success\":false}");
                            }
                        } else {
                            System.out.println("FEHLER IM PROTOKOLL");
                        }
                        out.flush();
                    }
                    case 1 -> { // spielmodus auswählen
                        switch (subState){
                            case 0 -> { // sende spielmodi-optionen
                                out.println(spielOptionenMSG);
                                subState = 1;
                            }
                            case 1 -> { // verarbeite auswahl
                                if(request.get("type").equals("modeselect")){
                                    switch (request.getInt("mode") * 10){
                                        case 0 -> { // random gegner
                                            // TODO hier funktioniert noch nichts
                                            if(server.lookingForOpponent(this)){
                                                out.println("{\"type\":\"queueNotification\",\"ready\":true}");
                                                this.gegnerGefunden = true;
                                            } else {
                                                out.println("{\"type\":\"queueNotification\",\"ready\":false}");
                                                waiting(in);
                                                out.println("{\"type\":\"queueNotification\",\"ready\":true}");
                                            }
                                            state = 2;
                                            subState = 0;
                                        }
                                        case 10 -> { // freund beitreten
                                            if(server.joinPrivate(this, request.getLong("uuid"))){ // uuid valid
                                                out.println("{\"type\":\"uuidResponse\",\"valid\":true}");
                                                this.gegnerGefunden = true;
                                                state = 2;
                                                subState = 0;
                                            } else { // uuid nicht valid
                                                out.println("{\"type\":\"uuidResponse\",\"valid\":false}");
                                            } // hiernach sind wir im loop heißt: input -> modeselect -> etc.

                                        }
                                        case 20 -> { // private lobby erstellen
                                            out.println(String.format("{\"type\":\"uuid\",\"uuid\":%d}", this.UUID));
                                            server.waitingPrivate(this);
                                            waiting(in);
                                        }
                                    }
                                } else {
                                    System.out.println("FEHLER IM PROTOKOLL");
                                }
                            }
                        }
                    }
                    case 2 -> { // Im Spiel
                        switch (subState){
                            case 0 -> { // informieren
                                out.println();
                            }
                            case 1 -> {
                                if(this.dran){
                                    out.println();
                                } else {

                                }
                            }
                        }
                    }
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

    public JSONObject waiting(BufferedReader in){
        try {
            while (!this.gegnerGefunden) {
                Thread.sleep(10);
                if(in.ready()){
                    JSONObject req = new JSONObject(in.readLine());
                    if(req.getString("type").equals("terminate")){
                        // TODO handle terminate
                    } else {
                        return req;
                    }
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("FEHLER IM SERVER");
        }
        return new JSONObject("");
    }

    public void giveGame(Game game){
        this.game = game;
        this.gegnerGefunden = true;
    }

    public long getUUID() {
        return this.UUID;
    }

    public void gegnerGefunden(){
        this.gegnerGefunden = true;
    }

    public void stoppe() {
        this.shouldRun = false;
    }

}
