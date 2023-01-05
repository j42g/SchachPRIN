package io.server.benutzerverwalktung;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class BenutzerManager {

    private final String filename;
    private final ArrayList<Benutzer> benutzer;

    public BenutzerManager(String filename) {
        this.filename = filename;
        this.benutzer = new ArrayList<Benutzer>();
        try {
            Path filepath = Path.of(filename);
            String benutzer = Files.readString(filepath);
            JSONArray benutzerJSONArr = new JSONArray(benutzer);
            for (Object benutzerJSON : benutzerJSONArr) {
                this.benutzer.add(new Benutzer((JSONObject) benutzerJSON));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existiertBenutzer(String name) {
        for (Benutzer benutzer : this.benutzer) {
            if (benutzer.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Benutzer einloggen(String name, byte[] password) {
        for (Benutzer benutzer : this.benutzer) {
            if (benutzer.getName().equals(name)) { // richtiger nutzer
                if (Arrays.equals(benutzer.getPassword(), password)) { // passwort richtig
                    return benutzer;
                } else { // passwort falsch
                    return null;
                }
            }
        } // nutzername existiert nicht
        return null;
    }

    public Benutzer registrieren(String name, byte[] password) {
        if (existiertBenutzer(name)) {
            return null;
        }
        Benutzer neu = new Benutzer(name, password, 1000, -1);
        this.benutzer.add(neu);
        return neu;
    }

    public Benutzer getNutzer(String name) {
        for (Benutzer nutzer : this.benutzer) {
            if (nutzer.getName().equals(name)) {
                return nutzer;
            }
        }
        return null;
    }

    public void abspeichern() {
        JSONArray a = new JSONArray();
        for (Benutzer b : benutzer) {
            a.put(b.toJSONObject());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename))) {
            writer.write(a.toString());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
