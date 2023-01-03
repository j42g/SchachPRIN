package spiel.figur;

public class Dame extends Figur {

    private int[][] moveSet;
    private int[][][] rayMoveSet;

    public Dame(int farbe) {
        super(farbe);

        int[][][] result = new int[8][7][2];
        result[0][0] = new int[]{1, 0};
        result[1][0] = new int[]{0, 1};
        result[2][0] = new int[]{-1, 0};
        result[3][0] = new int[]{0, -1};
        result[4][0] = new int[]{1, 1};
        result[5][0] = new int[]{1, -1};
        result[6][0] = new int[]{-1, -1};
        result[7][0] = new int[]{-1, 1};
        rayMoveSet = axis(result);
        moveSet = breakDown(rayMoveSet);
    }


    public String toString() {
        return "D";
        /*
        if (getFarbe() == -1){
            return "\u2655";
        } else {
            return"\u265B";
        }

         */
    }

    @Override
    public int[][] getOffsets() {
        return moveSet;
    }

}
