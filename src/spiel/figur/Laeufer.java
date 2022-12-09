package spiel.figur;

import spiel.feld.Quadrat;

public class Laeufer extends Figur {

    public Laeufer(int farbe) {
        super(farbe);
    }

    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }

}
