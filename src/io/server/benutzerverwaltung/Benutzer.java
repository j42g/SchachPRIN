package io.server.benutzerverwaltung;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

public class Benutzer {

    private final String name;
    private final byte[] password;
    private int elo;
    private long uuidOffenesSpiel;

    public Benutzer(JSONObject benutzer) {
        this.name = (String) benutzer.get("name");
        this.password = new byte[32];
        Iterator<Object> it = benutzer.getJSONArray("password").iterator();
        int temp;
        for (int i = 0; it.hasNext(); i++) {
            temp = (int) it.next();
            this.password[i] = (byte) temp;
        }
        this.elo = benutzer.getInt("elo");
        this.uuidOffenesSpiel = benutzer.getLong("uuidOffenesSpiel");
    }

    public Benutzer(String name, byte[] password, int elo, long uuid) {
        this.name = name;
        this.password = Arrays.copyOf(password, password.length);
        this.elo = elo;
        this.uuidOffenesSpiel = uuid;
    }

    public boolean hatAktivesSpiel() {
        return this.uuidOffenesSpiel != -1;
    }

    public JSONObject toJSONObject() {
        JSONObject benutzer = new JSONObject();
        benutzer.put("name", this.name);
        benutzer.put("password", this.password);
        benutzer.put("uuidOffenesSpiel", this.uuidOffenesSpiel);
        benutzer.put("elo", this.elo);
        return benutzer;
    }

    public String getName() {
        return name;
    }

    public byte[] getPassword() {
        return password;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public long getUuidOffenesSpiel() {
        return uuidOffenesSpiel;
    }

    public void setUuidOffenesSpiel(long uuid) {
        this.uuidOffenesSpiel = uuid;
    }
}
