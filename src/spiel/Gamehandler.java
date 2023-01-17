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
            System.out.println(feld.isWon());
            System.out.println(feld.isDrawn());
            System.out.println(feld.getFiftyMoveRule());
            System.out.println(feld.threeFoldRepetition());
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
