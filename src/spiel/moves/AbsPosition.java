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
        String AbcPos = "";
        String NPos = "";
        switch (x){
            case 0: AbcPos = "A";
            case 1: AbcPos = "B";
            case 2: AbcPos = "C";
            case 3: AbcPos = "D";
            case 4: AbcPos = "E";
            case 5: AbcPos = "F";
            case 6: AbcPos = "G";
            case 7: AbcPos = "H";
        }
        switch (y){
            case 0: NPos = "1";
            case 1: NPos = "2";
            case 2: NPos = "3";
            case 3: NPos = "4";
            case 4: NPos = "5";
            case 5: NPos = "6";
            case 6: NPos = "7";
            case 7: NPos = "8";
        }

        return AbcPos + " " + NPos;
    }
    public boolean equals(Object o){
        if(o instanceof AbsPosition){
            AbsPosition a = (AbsPosition) o;
            if(a.getX() == this.x && a.getY() == this.y){
                return true;
            }
        }
        return false;
    }
}
