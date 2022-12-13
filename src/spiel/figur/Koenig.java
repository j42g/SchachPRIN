package spiel.figur;

import spiel.feld.Quadrat;

public class Koenig extends Figur {

    public Koenig(int farbe) {
        super(farbe);
    }

    public String toString() {
        if (getFarbe() == -1){
            return "\u2654";
        } else {
            return "\u265A";
        }
    }
    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }
}
