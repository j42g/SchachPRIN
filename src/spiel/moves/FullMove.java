package spiel.moves;

public class FullMove {
    private AbsPosition pos;
    private Move mov;
    public FullMove(AbsPosition pos, Move mov){
        this.pos = pos;
        this.mov = mov;
    }

    public AbsPosition getPos() {
        return pos;
    }

    public Move getMov() {
        return mov;
    }
}
