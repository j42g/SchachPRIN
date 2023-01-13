package io.server.spiel;

import io.server.benutzerverwaltung.Benutzer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class SpielSpeicher {

    private final String filename;
    private final ArrayList<JSONObject> spiele;

    public SpielSpeicher(String filename) {
        this.filename = filename;
        this.spiele = new ArrayList<JSONObject>();
        try {
            Path filepath = Path.of(filename);
            String spieleFile = Files.readString(filepath);
            JSONArray benutzerJSONArr = new JSONArray(spieleFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkIfExists(long uuid) {
        return false;
    }
}
