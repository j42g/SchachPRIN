package spiel.figur;

public class Bauer extends Figur {

    public Bauer(int farbe) {
        super(farbe);
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
