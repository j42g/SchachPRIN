package io.server.spiel;

import io.server.benutzerverwaltung.Benutzer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class SpielSpeicher {

    private final String filename;
    private final ArrayList<Spiel> spiele;

    public SpielSpeicher(String filename) {
        this.filename = filename;
        this.spiele = new ArrayList<Spiel>();
        try {
            Path filepath = Path.of(filename);
            String spieleFile = Files.readString(filepath);
            JSONArray benutzerJSONArr = new JSONArray(spieleFile);
            for (Object spielObj : benutzerJSONArr) {
                spiele.add(new Spiel((JSONObject) spielObj));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfExists(long uuid) {
        for (Spiel s : spiele) {
            if (s.getUuid() == uuid) {
                return true;
            }
        }
        return false;
    }

    public Spiel getSpiel(long uuid) {
        for (Spiel s : spiele) {
            if (s.getUuid() == uuid) {
                return s;
            }
        }
        return null;
    }

    public void addSpiel(Spiel spiel) {
        this.spiele.add(spiel);

    }

    public void abspeichern() {
        JSONArray a = new JSONArray();
        for (Spiel s : new ArrayList<Spiel>(spiele)) {
            a.put(s.toJSONObject());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename))) {
            writer.write(a.toString());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
