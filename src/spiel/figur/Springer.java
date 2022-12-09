package spiel.figur;

import spiel.feld.Quadrat;

public class Springer extends Figur {

    public Springer(int farbe) {
        super(farbe);
    }

    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }

}
