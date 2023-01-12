import io.client.Client;
import io.server.ServerVerwaltung;
import spiel.feld.*;
import spiel.moves.AbsPosition;
import spiel.moves.ActualMoves;

public class Main {

    public static void main(String[] args) {
        Feld a = new Feld();
        ActualMoves b = new ActualMoves(a);
        System.out.println(b.computeMoves(new AbsPosition(6,0)));
    }

}