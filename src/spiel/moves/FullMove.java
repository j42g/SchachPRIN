package spiel.moves;

import spiel.feld.Feld;
import spiel.feld.Quadrat;
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
    public String toString() {
        if(promotionpiece != null){
            return pos.toString()+pos.addMove(mov).toString()+promotionpiece;
        } else {
            return pos.toString()+pos.addMove(mov).toString();
        }
    }

    public static boolean isValidNotation(String move) {
        if (move.length() != 4 && move.length() != 5) { // falsche laenge
            return false;
        }
        if(move.length() == 5) {
            if("kbrq".indexOf(move.charAt(4)) == -1) { // promotion piece falsch
                return false;
            }
        }
        return Quadrat.isValidQuadrat(move.substring(0, 3)) && Quadrat.isValidQuadrat(move.substring(3, 5));
    }
}
