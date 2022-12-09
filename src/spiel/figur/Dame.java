package spiel.figur;

import spiel.feld.Quadrat;

public class Dame extends Figur {

    public Dame(int farbe) {
        super(farbe);
    }

    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }

}
