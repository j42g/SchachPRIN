package spiel;

import spiel.feld.Feld;
import spiel.moves.AbsPosition;
import spiel.moves.FullMove;
import spiel.moves.Move;

import java.util.Scanner;

public class Gamehandler {
    public int playerturn;
    Scanner s = new Scanner(System.in);

    Feld feld;
    public Gamehandler() {
        feld = new Feld();
        //feld = new Feld("r3k2r/pppb1p1p/3p1qp1/2b1p1N1/2BnP3/N2P4/PPPB1PPP/R3K2R b KQkq h6 1 10");
        gameLogic();
    }



    public AbsPosition convertAbs(String a) {
        String[] res = a.split(" ");
        return new AbsPosition(Integer.parseInt(res[0]), Integer.parseInt(res[1]));
    }

    public Move convertMov(String a) {
        String[] res = a.split(" ");
        return new Move(Integer.parseInt(res[0]), Integer.parseInt(res[1]));
    }

    public void gameLogic() {
        while (true) {
            System.out.println(feld);
            System.out.println(feld.isWon() + " "+feld.isDrawn());
            System.out.println(feld.toFen());
            System.out.println(feld.getAllActuallyPossibleMoves(feld.playerTurn));
            String temp = "";
            System.out.print("move: ");
            temp = s.nextLine();
            FullMove a = feld.parseMove(temp);
            if(a==null){
                System.out.println("Invalid move");
                continue;
            }
            feld.move(a);
            System.out.println(feld.getMoveRecord());
        }
    }
}
