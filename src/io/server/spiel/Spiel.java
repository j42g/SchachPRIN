package io.server.spiel;

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


}
