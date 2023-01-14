package io.server.spiel;

import org.json.JSONObject;

public class Spiel {

    private final String white;
    private final String black;
    private final long uuid;
    private String fen;

    public Spiel(String white, String black, long uuid, String fen) {
        this.white = white;
        this.black = black;
        this.uuid = uuid;
        this.fen = fen;
    }

    public Spiel(JSONObject json) {
        this.white = json.getString("white");
        this.black = json.getString("black");
        this.uuid = json.getLong("uuid");
        this.fen = json.getString("fen");
    }

    public JSONObject toJSONObject() {
        JSONObject benutzer = new JSONObject();
        benutzer.put("white", this.white);
        benutzer.put("black", this.black);
        benutzer.put("uuid", this.uuid);
        benutzer.put("fen", this.fen);
        return benutzer;
    }

    public String getWhite() {
        return white;
    }

    public String getBlack() {
        return black;
    }

    public long getUuid() {
        return uuid;
    }

    public String getFen() {
        return fen;
    }
}
