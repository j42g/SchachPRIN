package spiel.figur;

public class Springer extends Figur {



    public Springer(int farbe) {
        super(farbe);
        moveSet.addMove(1,2);
        moveSet.addMove(1,-2);
        moveSet.addMove(-1,2);
        moveSet.addMove(-1,-2);
        moveSet.addMove(2,1);
        moveSet.addMove(2,-1);
        moveSet.addMove(-2,1);
        moveSet.addMove(-2,-1);
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
            return "N";
        } else {
            return "n";
        }
    }

    public String toString() {
        if (getFarbe() == 1){
            return"\u2658";
        } else {
            return "\u265E";
        }
    }

}
