package spiel.figur;

import spiel.feld.Quadrat;

public class Koenig extends Figur {

    public Koenig(int farbe) {
        super(farbe);
    }

    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }
}
