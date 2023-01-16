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
            String schlagen="";
            if(a.getFigAtPos(pos.addMove(mov))!=null){
                schlagen = "x";
            } else {
                schlagen = "-";
            }
                if(!(a.getFigAtPos(pos) instanceof Bauer)){
                    this.longNotation = a.getFigAtPos(pos).toString()+pos.toString()+schlagen+pos.addMove(mov).toString();
                } else {
                    this.longNotation = pos.toString()+schlagen+pos.addMove(mov).toString()+"todopromotion";
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
