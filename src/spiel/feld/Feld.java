package spiel.feld;

import spiel.figur.*;
import spiel.moves.*;

import java.util.ArrayList;

public class Feld {

    public Quadrat[][] feld;
    public String systemmessage;
    private ActualMoves checker = new ActualMoves(this);
    public static final int WEISS = 1;
    public static final int SCHWARZ = -1;
    private MoveRecord allMoves;


    public Feld() {
        allMoves = new MoveRecord();
        feld = new Quadrat[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
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
            feld[x][1] = new Quadrat(new Bauer(WEISS));
            feld[x][0] = new Quadrat(reihenfolgeW[x]);
        }
    }

    public Feld(MoveRecord a) {
        this();
        for (FullMove box : a.getMoves()) {
            move(box);
        }
    }

    public Feld(String fen) {
        // TODO
    }

    public boolean isInCheck(int color) {
        return getAllPossibleMoves(-color).contains(getKingPos(color));
    }

    public void updateField() {
        System.out.println("\r");
        System.out.println(systemmessage);
        System.out.println(this);
    }

    public void move(FullMove a) {
        move(a.getPos(), a.getMov());
    }

    public void move(AbsPosition a, AbsPosition b) {
        move(a, new AbsPosition(a.getX() - b.getX(), a.getY() - b.getY()));
    }

    public void move(AbsPosition a, Move b) {
        ArrayList<AbsPosition> temp = checker.computeMoves(a);
        if (temp.contains(a.addMove(b))) {
            setFigAtPos(a.addMove(b), getFigAtPos(a));
            setFigAtPos(a, null);
            getFigAtPos(a.addMove(b)).moved();
        }
    }

    public ArrayList<AbsPosition> getAllPossibleMoves(int color) {
        ArrayList<AbsPosition> res = new ArrayList<AbsPosition>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (feld[x][y].hasFigur()) {
                    if (feld[x][y].getFigur().getFarbe() == color) {
                        res.addAll(checker.computeMovesBack(new AbsPosition(x, y), true));
                    }
                }
            }
        }
        return res;
    }

    public AbsPosition getKingPos(int color) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (feld[x][y].hasFigur()) {
                    if (feld[x][y].getFigur() instanceof Koenig) {
                        if (feld[x][y].getFigur().getFarbe() == color) {
                            return new AbsPosition(x, y);
                        }
                    }
                }
            }
        }
        return new AbsPosition(-1, -1);
    }

    public Figur getFigAtPos(AbsPosition pos) {
        return feld[pos.getX()][pos.getY()].getFigur();
    }

    public void setFigAtPos(AbsPosition pos, Figur fig) {
        feld[pos.getX()][pos.getY()].addFigur(fig);
    }

    public String toFenNot() {
        int temp = 0;
        String res = "";
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                if (feld[x][y].getFigur() == null) {
                    temp++;
                } else {
                    if (temp != 0) {
                        res += temp;
                        temp = 0;
                    }
                    res += toRightNot(feld[x][y].getFigur().toString(), feld[x][y].getFigur().getFarbe());
                }
            }
            if (temp != 0) {
                res += temp;
                temp = 0;
            }
            if (y != 0) {
                res += "/";
            }
        }
        return res;
    }

    public String toRightNot(String a, int color) {
        String res = "";
        switch (a) {
            case ("T") -> {
                res = "R";
            }
            case ("L") -> {
                res = "B";
            }
            case ("S") -> {
                res = "N";
            }
            case ("D") -> {
                res = "Q";
            }
            case ("K") -> {
                res = "K";
            }
            case ("B") -> {
                res = "P";
            }
        }
        if (color == -1) {
            res = Character.toLowerCase(res.charAt(0)) + "";
        }
        return res;
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
