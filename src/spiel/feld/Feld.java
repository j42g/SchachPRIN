package spiel.feld;

import spiel.figur.*;

public class Feld {

    public Figur[][] feld;

    public static final int WEISS = 1;
    public static final int SCHWARZ = -1;


    public Feld() {
        feld = new Figur[8][8];
        Figur[] reihenfolgeW = new Figur[]{new Turm(WEISS), new Springer(WEISS), new Laeufer(WEISS), new Dame(WEISS), new Koenig(WEISS), new Laeufer(WEISS), new Springer(WEISS), new Turm(WEISS)};
        Figur[] reihenfolgeS = new Figur[]{new Turm(SCHWARZ), new Springer(SCHWARZ), new Laeufer(SCHWARZ), new Dame(SCHWARZ), new Koenig(SCHWARZ), new Laeufer(SCHWARZ), new Springer(SCHWARZ), new Turm(SCHWARZ)};
        for (int x = 0; x < 8; x++) { // schwarz
            feld[x][7] = reihenfolgeS[x];
            feld[x][6] = new Bauer(SCHWARZ);
        }
        for (int x = 0; x < 8; x++) { // weiß
            feld[x][1] = new Bauer(WEISS);
            feld[x][0] = reihenfolgeW[x];
        }
    }

    public void move(int[] a, int[] b) {

        if (contains(feld[a[0]][a[1]].getOffsets(), b)) {
            b = addArr(b, a);
            if (b[0] < 0 || b[1] < 0 || b[0] >= 8 || b[1] >= 8) {
                System.out.println("Out of bounds");
                return;
            }
            feld[b[0]][b[1]] = feld[a[0]][a[1]];
            feld[a[0]][a[1]] = null;
        } else {
            System.out.println("Not allowed");
        }
    }

    public boolean contains(int[][] data, int[] sample) {
        for (int i = 0; i < data.length; i++) {
            if (compArr(sample, data[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean compArr(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public static int[] addArr(int[] a, int[] b) {
        if (a.length != b.length) {
            return new int[0];
        }
        for (int i = 0; i < a.length; i++) {
            a[i] += b[i];
        }
        return a;
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 7; i > -1; i--) {
            for (int j = 0; j < 8; j++) {
                if (feld[j][i] != null) {
                    res += feld[j][i];
                } else {
                    res += " ";
                }
            }
            res += "\n";
        }
        return res;
    }

}
