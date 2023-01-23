package spiel.moves;

import java.util.ArrayList;

public class MovePattern {
    private ArrayList<Move> moves;

    public MovePattern(ArrayList<Move> moves) {
        this.moves = moves;
    }

    public MovePattern() {
        this.moves = new ArrayList<Move>();
    }

    public void addMove(Move move) {
        moves.add(move);
    }
    public void addMove(int x, int y){
         moves.add(new Move(x,y));
    }
    public void addRayMove(int x, int y){
        moves.add(new Move(x,y,true));
    }
    public ArrayList<Move> getMovePattern(){
        return moves;
    }
}
