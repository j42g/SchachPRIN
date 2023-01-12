package spiel.moves;

import java.util.ArrayList;

public class MoveRecord {
    ArrayList<FullMove> moves;

    public MoveRecord(){
        moves = new ArrayList<FullMove>();
    }
    public MoveRecord(ArrayList<FullMove> a){
        moves = a;
    }
    public void addMove(FullMove a){
        moves.add(a);
    }
    public ArrayList<FullMove> getMoves() {
        return moves;
    }
}
