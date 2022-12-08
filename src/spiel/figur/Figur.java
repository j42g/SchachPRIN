package spiel.figur;

import spiel.feld.Quadrat;

public abstract class Figur {

    private final int farbe;
    private Quadrat pos;

    public Figur(int farbe){
        this.farbe = farbe;
    }

    public void bewegeZu(Quadrat pos){
        this.position = pos;
    }

    public Quadrat[] getVerfuegbareFelder(){
        if()
    };

    public int getFarbe(){
        return this.farbe;
    }

}
