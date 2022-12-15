package spiel.figur;

public class Koenig extends Figur {
    private int[][] moveset;
    public Koenig(int farbe) {
        super(farbe);
        int[][][] result = new int[8][1][2];
        result[0][0]=new int[]{1,0};
        result[1][0]=new int[]{0,1};
        result[2][0]=new int[]{-1,0};
        result[3][0]=new int[]{0,-1};
        result[4][0]=new int[]{1,1};
        result[5][0]=new int[]{1,-1};
        result[6][0]=new int[]{-1,-1};
        result[7][0]=new int[]{-1,1};
        moveset=breakDown(result);

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
    @Override
    public int[][] getOffsets() {
        return moveset;
    }
}
