package spiel.figur;

public class Koenig extends Figur {
    private boolean hasMoved = false;
    public Koenig(int farbe) {
        super(farbe);
        moveSet.addMove(0,1);
        moveSet.addMove(1,0);
        moveSet.addMove(-1,0);
        moveSet.addMove(0,-1);
        moveSet.addMove(1,1);
        moveSet.addMove(1,-1);
        moveSet.addMove(-1,1);
        moveSet.addMove(-1,-1);
    }

    @Override
    public void moved() {
        hasMoved = true;
    }

    @Override
    public boolean getHasMoved() {
        return this.hasMoved;
    }
    @Override
    public String toLetter() {
        if(farbe == 1){
            return "K";
        } else {
            return "k";
        }
    }

    public String toString() {
        if (getFarbe() == -1){
            return "\u2654";
        } else {
            return "\u265A";
        }
    }
}
