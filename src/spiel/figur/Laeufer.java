package spiel.figur;

import spiel.feld.Quadrat;

public class Laeufer extends Figur {

    public Laeufer(int farbe) {
        super(farbe);
    }
    public String toString() {
        if (getFarbe() == -1){
            return "\u2657";
        } else {
            return "\u265D";
        }
    }
    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }

}
