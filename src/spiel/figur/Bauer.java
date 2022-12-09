package spiel.figur;

import spiel.feld.Quadrat;

public class Bauer extends Figur {

    public Bauer(int farbe) {
        super(farbe);
    }

    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }

}
