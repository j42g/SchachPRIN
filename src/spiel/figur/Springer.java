package spiel.figur;

public class Springer extends Figur {
    private int[][] moveset;
    public Springer(int farbe) {
        super(farbe);
        int[][]result = new int[8][2];
        result[0]=new int[]{1,2};
        result[1]=new int[]{1,-2};
        result[2]=new int[]{2,1};
        result[3]=new int[]{2,-1};
        result[4]=new int[]{-1,2};
        result[5]=new int[]{-1,-2};
        result[6]=new int[]{-2,-1};
        result[7]=new int[]{-2,1};
        moveset=result;
    }
    @Override
    public int[][] getOffsets() {
        return new int[0][0];
    }
    public String toString() {
        return "S";
        /*
        if (getFarbe() == -1){
            return"\u2658";
        } else {
            return "\u265E";
        }

         */
    }



}
