package spiel.figur;

public class Bauer extends Figur {
    private boolean hasMoved = false;

    public Bauer(int farbe) {
        super(farbe);
        moveSet.addBauerMove(1,farbe);
        moveSet.addBauerMove(-1,farbe);
        moveSet.addBauerMove(0,farbe);
        moveSet.addBauerMove(0,farbe*2);
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
    public String toString() {
        return "B";
        /*
        if (getFarbe() == -1){
            return "\u2659";
        } else {
            return "\u265F";
        }

         */
    }

}
