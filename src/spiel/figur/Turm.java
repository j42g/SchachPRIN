package spiel.figur;

import spiel.feld.Quadrat;

public class Turm extends Figur {

    public Turm(int farbe) {
        super(farbe);
    }

    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }

}
