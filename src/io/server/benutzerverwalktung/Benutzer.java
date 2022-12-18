package io.server.benutzerverwalktung;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

public class Benutzer {

    private final String name;
    private final byte[] password;
    private final long uuidOffenesSpiel;

    public Benutzer(JSONObject benutzer) {
        this.name = (String) benutzer.get("name");
        this.password = new byte[32];
        Iterator<Object> it = benutzer.getJSONArray("password").iterator();
        int temp;
        for (int i = 0; it.hasNext(); i++) {
            temp = (int) it.next();
            this.password[i] = (byte) temp;
        }
        this.uuidOffenesSpiel = benutzer.getLong("uuidOffenesSpiel");
    }

    public Benutzer(String name, byte[] password, long uuid) {
        this.name = name;
        this.password = Arrays.copyOf(password, password.length);
        this.uuidOffenesSpiel = uuid;
    }

    public boolean hatAktivesSpiel() {
        return this.uuidOffenesSpiel == -1;
    }

    public JSONObject toJSONObject() {
        JSONObject benutzer = new JSONObject();
        benutzer.put("name", this.name);
        benutzer.put("password", this.password);
        benutzer.put("uuidOffenesSpiel", this.uuidOffenesSpiel);
        return benutzer;
    }

    public String getName() {
        return name;
    }

    public byte[] getPassword() {
        return password;
    }

    public long getUuidOffenesSpiel() {
        return uuidOffenesSpiel;
    }
}
