import io.client.Client;
import io.server.ServerVerwaltung;
import spiel.Gamehandler;
import spiel.feld.*;
import spiel.moves.AbsPosition;
import spiel.moves.ActualMoves;
import spiel.moves.FullMove;
import spiel.moves.Move;

public class Main {

    public static void main(String[] args) {

        Feld f = new Feld();
        System.out.println(f.toFen());
        System.out.println(f);
        System.out.println(f.getFigAtPos(new AbsPosition("d8")));


    }
}