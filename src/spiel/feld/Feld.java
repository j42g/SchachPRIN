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
    private MoveRecord moveRecord;

    public Feld(Feld feld) {
        this.feld = feld.feld;
        this.systemmessage = feld.systemmessage;
        this.checker = feld.checker;
        this.moveRecord = feld.getMoveRecord();
    }

    public Feld() {
        moveRecord = new MoveRecord();
        feld = new Quadrat[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                feld[x][y] = new Quadrat();
            }
        }
        Figur[] reihenfolgeW = new Figur[]{new Turm(WEISS), new Springer(WEISS), new Laeufer(WEISS), new Dame(WEISS), new Koenig(WEISS), new Laeufer(WEISS), new Springer(WEISS), new Turm(WEISS)};
        //Figur[] reihenfolgeW = new Figur[]{new Turm(WEISS), null, null, null, new Koenig(WEISS), new Laeufer(WEISS), new Springer(WEISS), new Turm(WEISS)}; //queencastle setup
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

    public boolean isInCheck(int color) {
        if (getAllPossibleMoves(-color).contains(getKingPos(color))) {
            return true;
        }
        return false;
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

    public Feld(MoveRecord a) {
        this();
        for (FullMove box : a.getMoves()) {
            move(box);
        }
    }

    public Feld(String fen) {
        // TODO
    }

    public Figur getFigAtPos(AbsPosition pos) {
        return feld[pos.getX()][pos.getY()].getFigur();
    }

    public void setFigAtPos(AbsPosition pos, Figur fig) {
        feld[pos.getX()][pos.getY()].addFigur(fig);
    }

    public void move(FullMove a) {
        move(a.getPos(), a.getMov());
    }

    public void move(AbsPosition a, AbsPosition b) {
        move(a, new AbsPosition(a.getX() - b.getX(), a.getY() - b.getY()));
    }

    public void move(AbsPosition a, Move b) {
        ArrayList<AbsPosition> temp = checker.computeMoves(a);
        if(getFigAtPos(a) instanceof Koenig && Math.abs(b.getxOffset())==2){
            int color = 1;
            if(a.getY()==7){
                color = -1;
            }
            if(b.getxOffset()>0){
                if(kingSideCastlePossible(color)){
                    int rookXPos = 7;
                    setFigAtPos(a.addMove(b), getFigAtPos(a));
                    setFigAtPos(a, null);
                    getFigAtPos(a.addMove(b)).moved();
                    setFigAtPos(a.addMove(new Move(b.getxOffset()/2,0)),getFigAtPos(new AbsPosition(rookXPos,a.getY())));
                    setFigAtPos(new AbsPosition(rookXPos,a.getY()),null);
                }
            } else {
                if (queenSideCastlePossible(color)){
                    int rookXPos = 0;
                    setFigAtPos(a.addMove(b), getFigAtPos(a));
                    setFigAtPos(a, null);
                    getFigAtPos(a.addMove(b)).moved();
                    setFigAtPos(a.addMove(new Move(b.getxOffset()/2,0)),getFigAtPos(new AbsPosition(rookXPos,a.getY())));
                    setFigAtPos(new AbsPosition(rookXPos,a.getY()),null);
                }
            }



        } else {
            if (temp.contains(a.addMove(b))) {
                setFigAtPos(a.addMove(b), getFigAtPos(a));
                setFigAtPos(a, null);
                getFigAtPos(a.addMove(b)).moved();
            }
        }
    }

    public MoveRecord getMoveRecord() {
        return moveRecord;
    }

    public boolean queenSideCastlePossible(int color) {
        if (color == 1) {
            color = 0;
        } else {
            color = 7;
        }
        if (!kinghasMoved(color) && !rookHasMoved(color, 0) && !horizontalStripHasFigur(1,3,color)) {
            ArrayList<AbsPosition> a = checker.computeMoves(new AbsPosition(4,color));
            if(a.contains(new AbsPosition(3,color))){
                return true;
            }
        }
        return false;
    }
    public boolean kingSideCastlePossible(int color) {
        if (color == 1) {
            color = 0;
        } else {
            color = 7;
        }
        if (!kinghasMoved(color) && !rookHasMoved(color, 1) && !horizontalStripHasFigur(5,6,color)) {
            ArrayList<AbsPosition> a = checker.computeMoves(new AbsPosition(4,color));
            if(a.contains(new AbsPosition(5,color))){
                return true;
            }
        }
        return false;
    }

    public boolean horizontalStripHasFigur(int x1,  int x2, int y) {
        for (int i = x1; i <= x2; i++) {
            if (feld[i][y].hasFigur()) {
                return true;
            }
        }
        return false;
    }
    public boolean isValidMove(FullMove move){
        if(checker.computeMoves(move.getPos()).contains(move.getPos().addMove(move.getMov()))){
            return true;
        }
        return false;
    }
    public boolean rookHasMoved(int color, int side) { //side 0 is queenside, side 1 is kingside
        if (side == 1) {
            side = 7;
        }
        if (feld[side][color].hasFigur()) {
            if (!feld[side][color].getFigur().getHasMoved() && feld[side][color].getFigur() instanceof Turm) {
                return false;
            }
        }
        return true;
    }

    public boolean kinghasMoved(int color) {
        if (feld[4][color].hasFigur()) {
            if (feld[4][color].getFigur() instanceof Koenig) {
                if (!feld[4][color].getFigur().getHasMoved()) {
                    return false;
                }
            }
        }
        return true;
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
