package spiel;

import spiel.feld.Feld;
import spiel.moves.AbsPosition;
import spiel.moves.Move;

import java.util.Scanner;

public class Gamehandler {
    public int playerturn;
    Scanner s = new Scanner(System.in);

    Feld feld;

    public Gamehandler() {
        this.playerturn = 1;
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
        while (feld.getAllPossibleMoves(playerturn).size() != 0) {
            System.out.println(feld);
            String temp = "";
            System.out.print("pos of Figure: ");
            temp = s.nextLine();
            AbsPosition pos = convertAbs(temp);
            if(feld.getFigAtPos(pos)==null){
                System.out.println("No figure there");
                continue;
            }
            System.out.println();
            System.out.print("move offset: ");
            temp = s.nextLine();
            System.out.println();
            Move mov = convertMov(temp);

            feld.move(pos,mov);

            playerturn = -playerturn;
        }
    }
}
