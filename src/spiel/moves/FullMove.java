package spiel.moves;

import spiel.feld.Feld;
import spiel.figur.Bauer;

public class FullMove {
    private AbsPosition pos;
    private Move mov;
    private String longNotation;
    private String promotionpiece;
    public FullMove(AbsPosition pos, Move mov, Feld a){
        this.pos = pos;
        this.mov = mov;
        if(a.getFigAtPos(pos)==null){
            System.out.println("Fullmove constructor error, no Figure at position");
        }
        this.promotionpiece = "";
    }
    public FullMove(AbsPosition pos, Move mov, Feld a, String promo){
        this(pos,mov,a);
        this.promotionpiece = promo;
    }

    public AbsPosition getPos() {
        return pos;
    }

    public Move getMov() {
        return mov;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FullMove) {
            FullMove a = (FullMove) o;
            if (a.toString().equals(this.toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if(promotionpiece != null){
            return pos.toString()+pos.addMove(mov).toString()+promotionpiece;
        } else {
            return pos.toString()+pos.addMove(mov).toString();
        }

    }
}
