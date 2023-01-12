package spiel.figur;

public class Turm extends Figur {

    private boolean hasMoved = false;

    public Turm(int farbe) {
        super(farbe);
        moveSet.addRayMove(0,1);
        moveSet.addRayMove(1,0);
        moveSet.addRayMove(-1,0);
        moveSet.addRayMove(0,-1);
    }

    @Override
    public void moved() {
        this.hasMoved = true;
    }

    public String toString() {
        return "T";
        /*
        if (getFarbe() == -1) {
            return "\u2656";
        } else {
            return "\u265C";
        }
         */
    }
    public boolean getHasMoved(){
        return this.hasMoved;
    }
}
