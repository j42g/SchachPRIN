package spiel.figur;

import spiel.moves.MovePattern;

public abstract class Figur {

    protected final int farbe;
    private String fenNotation;
    protected MovePattern moveSet = new MovePattern();

    public Figur(int farbe) {
        this.farbe = farbe;
    }

    public int getFarbe() {
        return this.farbe;
    }

    public MovePattern getMoveSet() {
        return moveSet;
    }

    public abstract void moved();

    public abstract boolean getHasMoved();

    public abstract String toLetter();

    public static Figur fromString(char piece) {
        int farbe = Character.isUpperCase(piece) ? 1 : -1;
        return switch (Character.toUpperCase(piece)) {
            case 'P' -> new Bauer(farbe);
            case 'N' -> new Springer(farbe);
            case 'B' -> new Laeufer(farbe);
            case 'R' -> new Turm(farbe);
            case 'Q' -> new Dame(farbe);
            case 'K' -> new Koenig(farbe);
            default -> null;
        };
    }

}
