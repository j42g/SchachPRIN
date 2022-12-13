package spiel.figur;

import spiel.feld.Quadrat;

import java.sql.SQLOutput;

public class Bauer extends Figur {

    public Bauer(int farbe) {
        super(farbe);
    }

    @Override
    public String toString() {
        if (getFarbe() == 1){
            return "\u2659";
        } else {
            return "\u265F";
        }
    }

    @Override
    public Quadrat[] getVerfuegbareFelder() {
        return new Quadrat[0];
    }

}
