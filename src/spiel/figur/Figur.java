package spiel.figur;

import spiel.feld.Quadrat;

public abstract class Figur {

    private final int farbe;
    private Quadrat position;

    public Figur(int farbe){
        this.farbe = farbe;
    }

    public void bewegeZu(Quadrat pos){
        this.position = pos;
    }

    abstract public Quadrat[] getVerfuegbareFelder();

    public boolean istGepinnt(){
        return false;
    }

    public int getFarbe(){
        return this.farbe;
    }

}
