package spiel.figur;

public class Bauer extends Figur {
    private boolean hasMoved = false;

    public Bauer(int farbe) {
        super(farbe);
        //moveSet.
    }

    @Override
    public void moved() {
        hasMoved = true;
        //moveSet.
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
