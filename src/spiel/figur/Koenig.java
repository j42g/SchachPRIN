package spiel.figur;

public class Koenig extends Figur {

    public Koenig(int farbe) {
        super(farbe);
    }

    public String toString() {
        return "K";
        /*
        if (getFarbe() == -1){
            return "\u2654";
        } else {
            return "\u265A";
        }

         */
    }
}
