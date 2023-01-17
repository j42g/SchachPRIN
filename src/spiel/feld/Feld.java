package spiel.feld;

import spiel.figur.*;
import spiel.moves.*;

import java.util.ArrayList;

public class Feld {

    public Quadrat[][] feld;
    public ActualMoves checker = new ActualMoves(this);
    public static final int WEISS = 1;
    public static final int SCHWARZ = -1;

    public int playerTurn;
    private ArrayList<FullMove> moveRecord;
    private AbsPosition enPassant;
    private int fiftyMoveRule;
    private int moveCount;
    private int gameState;
    private boolean QWCastling = true;
    private boolean QBCastling = true;
    private boolean KWCastling = true;
    private boolean KBCastling = true;

    public Feld(Quadrat[][] feld) {
        this.feld = feld;
    }


    public Feld() {
        playerTurn = 1;
        moveRecord = new ArrayList<FullMove>();
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

    public Feld(ArrayList<FullMove> a) {
        this();
        for (FullMove box : a) {
            move(box);
        }
    }

    public Feld(String fen) {
        String[] fenparts = fen.split(" ");
        // board
        int file;
        this.feld = new Quadrat[8][8];
        String[] ranks = fenparts[0].split("/");
        for (int rank = 0; rank < ranks.length; rank++) {
            file = 0;
            for (char c : ranks[rank].toCharArray()) {
                if ("rnbqkpRNBQKP".indexOf(c) != -1) {
                    feld[file][rank] = new Quadrat(Figur.fromString(c));
                    file++;
                } else if ("12345678".indexOf(c) != -1) {
                    file += c - 0x31;
                }
            }

        }
        // turn
        this.playerTurn = fenparts[1].equals("w") ? 1 : -1;
        this.KWCastling = fenparts[2].contains("K");
        this.QWCastling = fenparts[2].contains("Q");
        this.KBCastling = fenparts[2].contains("k");
        this.QBCastling = fenparts[2].contains("q");
        // En passant
        if (fenparts[3].equals("-")) {
            this.enPassant = null;
        } else {
            this.enPassant = new AbsPosition(fenparts[3]);
        }
        // fifty move rule
        this.fiftyMoveRule = Integer.parseInt(fenparts[4]);
        // total move count
        this.moveCount = Integer.parseInt(fenparts[5]);

    }

    public FullMove parseMove(String a) {
        AbsPosition origin = null;
        Move move = null;
        if (a.length() < 4 || a.length() > 5) {
            return null;
        }
        if (Character.isDigit(a.charAt(1))) {
            origin = new AbsPosition(a.substring(0, 2));
            if (origin.isPossible()) {
                    AbsPosition destination = new AbsPosition(a.charAt(2)+""+a.charAt(3));
                    if(destination.isPossible()){
                        move = new Move(origin, destination);
                        if (isValidMove(new FullMove(origin, move, this))) {
                            return new FullMove(origin, move, this);
                        }
                    }
            }
        }

        return null;
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

    public boolean move(AbsPosition a, Move b) {
        if (isValidMove(new FullMove(a, b, this))) {
            if (getFigAtPos(a) instanceof Koenig && Math.abs(b.getxOffset()) == 2) {
                if (b.getxOffset() > 0) {
                    int rookXPos = 7;
                    moveRecord.add(new FullMove(a, b, this));
                    setFigAtPos(a.addMove(b), getFigAtPos(a));
                    setFigAtPos(a, null);
                    getFigAtPos(a.addMove(b)).moved();
                    setFigAtPos(a.addMove(new Move(b.getxOffset() / 2, 0)), getFigAtPos(new AbsPosition(rookXPos, a.getY())));
                    setFigAtPos(new AbsPosition(rookXPos, a.getY()), null);
                    enPassant = null;
                    playerTurn = -playerTurn;
                    return true;
                } else {
                    moveRecord.add(new FullMove(a, b, this));
                    int rookXPos = 0;
                    setFigAtPos(a.addMove(b), getFigAtPos(a));
                    setFigAtPos(a, null);
                    getFigAtPos(a.addMove(b)).moved();
                    setFigAtPos(a.addMove(new Move(b.getxOffset() / 2, 0)), getFigAtPos(new AbsPosition(rookXPos, a.getY())));
                    setFigAtPos(new AbsPosition(rookXPos, a.getY()), null);
                    enPassant = null;
                    playerTurn = -playerTurn;
                    return true;
                }
            } else {
                moveRecord.add(new FullMove(a, b, this));
                if (getFigAtPos(a) instanceof Bauer && getFigAtPos(a.addMove(b)) == null && b.getxOffset() != 0) {
                    setFigAtPos(a.addMove(new Move(b.getxOffset(), 0)), null);

                }
                if (!(getFigAtPos(a) instanceof Bauer && Math.abs(b.getyOffset()) != 2)) {
                    enPassant = null;
                }
                setFigAtPos(a.addMove(b), getFigAtPos(a));
                setFigAtPos(a, null);
                getFigAtPos(a.addMove(b)).moved();
                playerTurn = -playerTurn;
                return true;
            }
        }
        return false;
    }

    public ArrayList<FullMove> getMoveRecord() {
        return moveRecord;
    }

    public boolean queenSideCastlePossible(int color) {
        if (color == 1) {
            color = 0;
        } else {
            color = 7;
        }
        if (!kinghasMoved(color) && !rookHasMoved(color, 0) && !horizontalStripHasFigur(1, 3, color)) {
            ArrayList<AbsPosition> a = checker.computeMoves(new AbsPosition(4, color));
            if (a.contains(new AbsPosition(3, color))) {
                if (color == 0) {
                    return true & QWCastling;
                } else {
                    return true & QBCastling;
                }
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
        if (!kinghasMoved(color) && !rookHasMoved(color, 1) && !horizontalStripHasFigur(5, 6, color)) {
            ArrayList<AbsPosition> a = checker.computeMoves(new AbsPosition(4, color));
            if (a.contains(new AbsPosition(5, color))) {
                if (color == 0) {
                    return true & KWCastling;
                } else {
                    return true & KBCastling;
                }
            }
        }
        return false;
    }

    public boolean horizontalStripHasFigur(int x1, int x2, int y) {
        for (int i = x1; i <= x2; i++) {
            if (feld[i][y].hasFigur()) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidMove(FullMove move) {
        if (checker.computeMoves(move.getPos()).contains(move.getPos().addMove(move.getMov()))) {
            if (getFigAtPos(move.getPos()).getFarbe() == playerTurn) {
                if (isInCheckAfterMove(move)) {
                    System.out.println("king cant be in check after own move");
                    return false;
                }
                return true;
            } else {
                System.out.println("Figure of wrong color");
                return false;
            }
        }
        System.out.println("move not possible");
        return false;
    }

    public boolean isInCheckAfterMove(FullMove fullMove) {
        Feld test = copyFeld();
        int color = test.getFigAtPos(fullMove.getPos()).getFarbe();
        test.noTestMove(fullMove);
        if (test.isInCheck(color)) {
            return true;
        }
        return false;
    }

    public void noTestMove(FullMove fullMove) { //moves a figure with almost no checks attached for simulating if king is in check after own move (illegal)
        setFigAtPos(fullMove.getPos().addMove(fullMove.getMov()), getFigAtPos(fullMove.getPos()));
        setFigAtPos(fullMove.getPos(), null);
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

    public Feld(boolean flag) {
        this.feld = new Quadrat[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                this.feld[x][y] = new Quadrat();
            }
        }
    }

    public Feld copyFeld() {
        Feld res = new Feld(true);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                res.feld[x][y].addFigur(this.feld[x][y].getFigur());
            }
        }
        return res;
    }

    public AbsPosition getEnPassant() {
        return enPassant;
    }

    public void setEnPassant(AbsPosition enPassant) {
        this.enPassant = enPassant;
    }

    @Override
    public String toString() {
        String res = "";
        int bmk = 1;
        int temp = 7;
        for (int i = 7; i > -1; i--) {


            for (int j = 0; j < 8; j++) {
                if (feld[j][i] != null) {
                    res += "|" +feld[j][i];

                    if (j==7){
                        res += "|";
                        if (i ==0){
                            res += "8";
                            res += "\n";
                            res += "|A|B|C|D|E|F|G|H|";
                        }
                    }
                    if(j ==7){
                        if(i==temp) {
                            if (bmk < 8) {
                                res += bmk + "";
                                bmk++;
                                temp--;
                            }
                        }

                    }
                }
            }
            res += "\n";
        }
        return res;
    }

}
