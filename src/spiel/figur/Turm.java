package spiel.figur;

public class Turm extends Figur {

    private int[][] moveset;

    public Turm(int farbe) {
        super(farbe);
        int[][][] result = new int[4][7][2];
        result[0][0] = new int[]{1, 0};
        result[1][0] = new int[]{0, 1};
        result[2][0] = new int[]{-1, 0};
        result[3][0] = new int[]{0, -1};
        moveset = breakDown(axis(result));
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


    @Override
    public int[][] getOffsets() {
        return moveset;
    }

}
