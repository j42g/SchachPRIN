package spiel.figur;

public abstract class Figur {

    protected final int farbe;


    public Figur(int farbe){
        this.farbe = farbe;
    }



    protected int[][] timesInt(int[][] arr){
        for(int i = 1; i<arr.length;i++){
            arr[i][0]=arr[0][0]*(i+1);
            arr[i][1]=arr[0][1]*(i+1);
        }
        return arr;
    }

    protected int[][] breakDown(int[][][] arr){
        int[][] res = new int[arr.length*arr[0].length][2];
        for(int i = 0; i<arr.length;i++){
            for(int j = 0; j<arr[0].length;j++){
                res[j+i*arr[0].length]=arr[i][j];
            }
        }
        return res;
    }
    protected int[][][] axis(int[][][] arr){
        for(int i = 0; i<arr.length;i++){
            arr[i]=timesInt(arr[i]);
        }
        return arr;
    }

    public abstract int[][] getOffsets();
   // public abstract boolean rays();

    public int getFarbe(){
        return this.farbe;
    }

}
