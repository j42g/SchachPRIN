package spiel.moves;

public class AbsPosition {
    private int x;
    private int y;

    public AbsPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public AbsPosition addMove(Move move) {
        return new AbsPosition(this.x + move.getxOffset(), this.y + move.getyOffset());
    }

    public boolean isPossible() {
        return !(x < 0 || x > 7 || y < 0 || y > 7);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public String toString(){
        return x+" "+y;
    }
}
