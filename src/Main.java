import io.client.Client;
import io.server.ServerVerwaltung;
import spiel.Gamehandler;
import spiel.feld.*;
import spiel.moves.AbsPosition;
import spiel.moves.ActualMoves;
import spiel.moves.Move;

public class Main {

    public static void main(String[] args) {
        Feld a = new Feld();
        System.out.println(a.toFenNot());

    }
}