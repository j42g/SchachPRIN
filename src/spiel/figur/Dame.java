package spiel.figur;

import spiel.feld.Quadrat;

public class Dame extends Figur {

    public Dame(int farbe) {
        super(farbe);
    }


    public String toString() {
        if (getFarbe() == -1){
            return "\u2655";
        } else {
            return"\u265B";
        }
    }
    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }

}
