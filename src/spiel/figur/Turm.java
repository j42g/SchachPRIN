package spiel.figur;

import spiel.feld.Feld;

public class Turm extends Figur {

    private boolean hasMoved = false;

    public Turm(int farbe) {
        super(farbe);
        moveSet.addRayMove(0, 1);
        moveSet.addRayMove(1, 0);
        moveSet.addRayMove(-1, 0);
        moveSet.addRayMove(0, -1);
    }

    @Override
    public void moved() {
        this.hasMoved = true;
    }

    @Override
    public String toLetter() {
        if (farbe == 1) {
            return "R";
        } else {
            return "r";
        }
    }
    public String toString() {
        if (getFarbe() == Feld.WEISS) {
            return "\u2656";
        } else {
            return "\u265C";
        }
    }
    public boolean getHasMoved(){
        return this.hasMoved;
    }
}
