package spiel.figur;

import spiel.feld.Quadrat;

public class Turm extends Figur {

    public Turm(int farbe) {
        super(farbe);
    }

    public String toString() {
        if (getFarbe() == -1) {
            return "\u2656";
        } else {
            return "\u265C";
        }
    }


    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }

}
