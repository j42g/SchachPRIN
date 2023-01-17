package spiel.figur;

public class Laeufer extends Figur {



    public Laeufer(int farbe) {
        super(farbe);
        moveSet.addRayMove(1,1);
        moveSet.addRayMove(1,-1);
        moveSet.addRayMove(-1,1);
        moveSet.addRayMove(-1,-1);
    }

    @Override
    public void moved() {

    }

    @Override
    public boolean getHasMoved() {
        return false;
    }

    @Override
    public String toLetter() {
        if(farbe == 1){
            return "B";
        } else {
            return "b";
        }
    }

    public String toString() {
        if (getFarbe() == -1){
            return "\u2657";
        } else {
            return "\u265D";
        }
    }
}
