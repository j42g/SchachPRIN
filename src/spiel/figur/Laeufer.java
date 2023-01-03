package spiel.figur;

public class Laeufer extends Figur {

    int[][] moveset;

    public Laeufer(int farbe) {
        super(farbe);
        int[][][] result = new int[4][7][2];
        result[0][0]=new int[]{1,1};
        result[1][0]=new int[]{1,-1};
        result[2][0]=new int[]{-1,-1};
        result[3][0]=new int[]{-1,1};
        moveset=breakDown(axis(result));
    }

    public String toString() {
        return "L";
        /*
        if (getFarbe() == -1){
            return "\u2657";
        } else {
            return "\u265D";
        }

         */
    }
    @Override
    public int[][] getOffsets() {
        return moveset;
    }

}
