package spiel.figur;

public class Dame extends Figur {

    public Dame(int farbe) {
        super(farbe);
        moveSet.addRayMove(0,1);
        moveSet.addRayMove(1,0);
        moveSet.addRayMove(-1,0);
        moveSet.addRayMove(0,-1);
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
            return "Q";
        } else {
            return "q";
        }
    }


    public String toString() {

        if (getFarbe() == 1){
            return "\u2655";
        } else {
            return"\u265B";
        }

    }

}
