package spiel.figur;

public class Bauer extends Figur {

    int[][] moveset = new int[][]{{0, farbe}, {0, farbe * 2}, {1, farbe}, {-1, farbe}};

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

    @Override
    public int[][] getOffsets() {
        return moveset;

    }

}
