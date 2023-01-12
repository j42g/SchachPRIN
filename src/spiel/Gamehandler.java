package spiel;
import spiel.feld.Feld;

import java.util.Scanner;
public class Gamehandler {
    public boolean playerturn;
    Scanner s = new Scanner(System.in);

    public Gamehandler(){
        this.playerturn = true;
        Feld a = new Feld();
        gameLogic();
    }
    public void gameLogic(){


    }
    public int[][] acceptMove(){
        int[][] res;
        System.out.println("X Koordinate der angewählten Figur?");
        int x = s.nextInt();
        System.out.println("Y Koordinate der angewählten Figur?");
        int y = s.nextInt();

        return new int[0][0];
    }
}
