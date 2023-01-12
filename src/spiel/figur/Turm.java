package spiel.figur;

public class Turm extends Figur {



    public Turm(int farbe) {
        super(farbe);
        moveSet.addRayMove(0,1);
        moveSet.addRayMove(1,0);
        moveSet.addRayMove(-1,0);
        moveSet.addRayMove(0,-1);
    }

    public String toString() {
        return "T";
        /*
        if (getFarbe() == -1) {
            return "\u2656";
        } else {
            return "\u265C";
        }
         */
    }
}
