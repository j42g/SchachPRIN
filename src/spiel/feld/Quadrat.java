package spiel.feld;

import spiel.figur.Figur;

public class Quadrat {

    private final int x;
    private final int y;
    private Figur figur;

    public Quadrat(int x, int y, Figur figur){
        this.figur = figur;
        this.x = x;
        this.y = y;
    }

    public Quadrat(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Figur getFigur(){
        return this.figur;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }
}
