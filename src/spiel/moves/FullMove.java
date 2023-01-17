package spiel.moves;

import spiel.feld.Feld;
import spiel.figur.Bauer;

public class FullMove {
    private AbsPosition pos;
    private Move mov;
    private String longNotation;
    public FullMove(AbsPosition pos, Move mov, Feld a){
        this.pos = pos;
        this.mov = mov;
        if(a.getFigAtPos(pos)!=null){
                if(!(a.getFigAtPos(pos) instanceof Bauer)){
                    this.longNotation = pos.toString()+pos.addMove(mov).toString();
                } else {
                    this.longNotation = pos.toString()+pos.addMove(mov).toString()+":";
                }
        }
    }

    public AbsPosition getPos() {
        return pos;
    }

    public Move getMov() {
        return mov;
    }

    @Override
    public String toString() {
        return longNotation;
    }
}
