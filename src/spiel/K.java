package spiel;

import spiel.feld.Quadrat;
import spiel.figur.*;

public class K { // Konstanten

    // Farben
    public static final int WEISS = 1;
    public static final int SCHWARZ = -1;

    // Figuren
    private static final Koenig koenigW = new Koenig(WEISS);
    private static final Koenig koenigS = new Koenig(SCHWARZ);

    private static final Dame dameW = new Dame(WEISS);
    private static final Dame dameS = new Dame(SCHWARZ);

    private static final Laeufer LaeuferW = new Laeufer(WEISS);
    private static final Laeufer LaeuferS = new Laeufer(SCHWARZ);

    private static final Springer SpringerW = new Springer(WEISS);
    private static final Springer SpringerS = new Springer(SCHWARZ);

    private static final Turm TurmW = new Turm(WEISS);
    private static final Turm TurmS = new Turm(SCHWARZ);

    private static final Bauer BauerW = new Bauer(WEISS);
    private static final Bauer BauerS = new Bauer(SCHWARZ);

    private static final Quadrat[][] spielBeginn = new Quadrat[8][8];
    static {
        Figur[] reihenfolgeW = new Figur[]{TurmW, SpringerW, LaeuferW, dameW, koenigW, LaeuferW, SpringerW, TurmW};
        Figur[] reihenfolgeS = new Figur[]{TurmS, SpringerS, LaeuferS, dameS, koenigS, LaeuferS, SpringerS, TurmS};
        for(int x = 0; x < 8; x++){ // schwarz
            spielBeginn[x][0] = new Quadrat(x, 0, reihenfolgeS[x]);
            spielBeginn[x][1] = new Quadrat(x, 1, BauerS);
        }
        for(int y = 2; y < 7; y++){ // leere Felder
            for(int x = 0; x < 8; x++){
                spielBeginn[x][y] = new Quadrat(x, y);
            }
        }
        for(int x = 0; x < 8; x++){ // weiß
            spielBeginn[x][1] = new Quadrat(x, 7, BauerW);
            spielBeginn[x][0] = new Quadrat(x, 8, reihenfolgeW[x]);
        }

    }

}
