package spiel.figur;

public class Bauer extends Figur {
    private boolean hasMoved = false;

    public Bauer(int farbe) {
        super(farbe);
        moveSet.addMove(1,farbe);
        moveSet.addMove(-1,farbe);
        moveSet.addMove(0,farbe);
        moveSet.addMove(0,farbe*2);
    }

    @Override
    public void moved() {
        if(!hasMoved) {
            hasMoved = true;
            moveSet.getMovePattern().remove(3);
        }
    }
    @Override
    public boolean getHasMoved() {
        return false;
    }

    @Override
    public String toLetter() {
        if(farbe == 1){
            return "P";
        } else {
            return ("p");
        }
    }

    @Override
    public String toString() {

        if (getFarbe() == 1){
            return "\u2659";
        } else {
            return "\u265F";
        }

    }

}
