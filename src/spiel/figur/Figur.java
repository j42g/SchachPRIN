package spiel.figur;

import spiel.moves.MovePattern;

public abstract class Figur {

    protected final int farbe;

    protected MovePattern moveSet = new MovePattern();
    public Figur(int farbe){
        this.farbe = farbe;
    }

    public int getFarbe(){
        return this.farbe;
    }

    public MovePattern getMoveSet(){
        return moveSet;
    }

}
