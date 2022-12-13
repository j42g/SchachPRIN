package spiel.figur;

import spiel.feld.Quadrat;

public class Springer extends Figur {

    public Springer(int farbe) {
        super(farbe);
    }
    public String toString() {
        if (getFarbe() == -1){
            return"\u2658";
        } else {
            return "\u265E";
        }
    }
    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }

}
