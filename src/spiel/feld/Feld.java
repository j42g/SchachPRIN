package spiel.feld;

import spiel.figur.*;
import spiel.moves.AbsPosition;
import spiel.moves.ActualMoves;
import spiel.moves.Move;

import java.util.ArrayList;

public class Feld {

    public Quadrat[][] feld;
    public String systemmessage;
    private ActualMoves checker = new ActualMoves(this);
    public static final int WEISS = 1;
    public static final int SCHWARZ = -1;



    public Feld() {
        feld = new Quadrat[8][8];
        for(int y = 0; y<8;y++){
            for(int x = 0; x<8;x++){
                feld[x][y] = new Quadrat();
            }
        }
        Figur[] reihenfolgeW = new Figur[]{new Turm(WEISS), new Springer(WEISS), new Laeufer(WEISS), new Dame(WEISS), new Koenig(WEISS), new Laeufer(WEISS), new Springer(WEISS), new Turm(WEISS)};
        Figur[] reihenfolgeS = new Figur[]{new Turm(SCHWARZ), new Springer(SCHWARZ), new Laeufer(SCHWARZ), new Dame(SCHWARZ), new Koenig(SCHWARZ), new Laeufer(SCHWARZ), new Springer(SCHWARZ), new Turm(SCHWARZ)};
        for (int x = 0; x < 8; x++) { // schwarz
            feld[x][7] = new Quadrat(reihenfolgeS[x]);
            feld[x][6] = new Quadrat(new Bauer(SCHWARZ));
        }

        for (int x = 0; x < 8; x++) { // weiß
            //feld[x][1] = new Quadrat(new Bauer(WEISS));
            feld[x][0] = new Quadrat(reihenfolgeW[x]);
        }
    }

    public Feld(String fen){
        // TODO
    }

    public Figur getFigAtPos(AbsPosition pos){
        return feld[pos.getX()][pos.getY()].getFigur();
    }
    public void updateField(){
        System.out.println("\r");
        System.out.println(systemmessage);
        System.out.println(this);
    }
    public void move(AbsPosition a, Move b){
        ArrayList<AbsPosition> temp = checker.computeMoves(a);

        return;
    }
    @Override
    public String toString() {
        String res = "";
        for (int i = 7; i > -1; i--) {
            for (int j = 0; j < 8; j++) {
                if (feld[j][i] != null) {
                    res += feld[j][i];
                }
            }
            res += "\n";
        }
        return res;
    }

}
