package spiel.feld;

import spiel.figur.*;
import spiel.moves.AbsPosition;
import spiel.moves.ActualMoves;
import spiel.moves.FullMove;
import spiel.moves.Move;

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
    private Figur promotionPiece;
    private int moveCount;
    private boolean QWCastling = true;
    private boolean QBCastling = true;
    private boolean KWCastling = true;
    private boolean KBCastling = true;


    public Feld() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public Feld(String fen) {
        String[] fenparts = fen.split(" ");
        // init moverecord
        this.moveRecord = new ArrayList<FullMove>();
        // board
        this.feld = new Quadrat[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                feld[x][y] = new Quadrat();
            }
        }
        int file;
        String[] ranks = fenparts[0].split("/");
        for (int rank = 0; rank < 8; rank++) {
            file = 0;
            for (char c : ranks[7 - rank].toCharArray()) {
                if ("rnbqkpRNBQKP".indexOf(c) != -1) {
                    feld[file][rank] = new Quadrat(Figur.fromString(c));
                    file++;
                } else if ("12345678".indexOf(c) != -1) {
                    file += c - 0x30;
                }
            }

        }
        // turn
        this.playerTurn = fenparts[1].equals("w") ? 1 : -1;
        // castling rights
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

    private Feld(boolean a) {
        this.feld = new Quadrat[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                feld[x][y] = new Quadrat();
            }
        }
    }

    private boolean isInCheck(int color) {
        return getAllTheoreticallyPossibleMoves(-color).contains(getKingPos(color)); //checks if king is in a position which an enemy piece can reach
    }

    private boolean isMate(int color) {
        return getAllActuallyPossibleMoves(color).size() == 0 && isInCheck(color); //checks if king is in checks and no moves change that
    }

    private Feld copyFeld() {
        Feld res = new Feld(true);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                res.feld[x][y].addFigur(this.feld[x][y].getFigur());
            }
        }
        return res;
    }

    private boolean threeFoldRepetition() {
        int lastIndex = moveRecord.size() - 1;
        if (moveRecord.size() >= 12) {
            return moveRecord.get(lastIndex).equals(moveRecord.get(lastIndex - 4)) && moveRecord.get(lastIndex).equals(moveRecord.get(lastIndex - 8)) && moveRecord.get(lastIndex - 2).equals(moveRecord.get(lastIndex - 6)) && moveRecord.get(lastIndex - 2).equals(moveRecord.get(lastIndex - 10)) && moveRecord.get(lastIndex - 1).equals(moveRecord.get(lastIndex - 5)) && moveRecord.get(lastIndex - 1).equals(moveRecord.get(lastIndex - 9)) && moveRecord.get(lastIndex - 3).equals(moveRecord.get(lastIndex - 7)) && moveRecord.get(lastIndex - 3).equals(moveRecord.get(lastIndex - 11));
        }
        return false;
    }

    private boolean fiftyMoveRuleExceeded() {
        return fiftyMoveRule >= 50;
    }

    private boolean insuficcientMaterial() {
        int[] springerlaeufer = new int[3]; //index 0 zählt springer, index 1 schwarze läufer und index 2 weiße läufer, die zweite dimension symbolisiert die farbe, 0 ist weiß und 1 schwarz
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (feld[x][y].getFigur() != null) {
                    Figur box = feld[x][y].getFigur();
                    if (box instanceof Bauer || box instanceof Dame || box instanceof Turm) {
                        return false;
                    }
                    if (box instanceof Springer) {
                        if (box.getFarbe() == 1) {
                            springerlaeufer[0]++;
                        }
                    }
                    if (box instanceof Laeufer) {
                        springerlaeufer[(x + y) % 2 + 1]++;
                    }
                }
            }
        }
        if (springerlaeufer[0] > 1) { //zu viele Springer
            return false;
        }
        return springerlaeufer[1] + springerlaeufer[2] <= 2 && (springerlaeufer[1] != 1 || springerlaeufer[2] != 1);
    }

    public int isWon() {
        if (isMate(1)) {
            return -1;
        } else if (isMate(-1)) {
            return 1;
        }
        return 0;
    }

    public boolean isDrawn() {
        return getAllActuallyPossibleMoves(playerTurn).size() == 0 && !isInCheck(playerTurn) || fiftyMoveRuleExceeded() || threeFoldRepetition() || insuficcientMaterial();
    }

    public FullMove parseMove(String a) {
        AbsPosition origin;
        Move move;
        if (a.length() < 4 || a.length() > 5) {
            System.out.println("Falsche Länge, moveParser error");
            return null;
        }
        if (Character.isDigit(a.charAt(1))) {
            origin = new AbsPosition(a.substring(0, 2));
            if (origin.isPossible()) {
                AbsPosition destination = new AbsPosition(a.charAt(2) + "" + a.charAt(3));
                if (destination.isPossible()) {
                    move = new Move(origin, destination);
                    if (isValidMove(new FullMove(origin, move, this))) {
                        if (!(getFigAtPos(origin) instanceof Bauer && (destination.getY() == 7 || destination.getY() == 0))) {
                            if (a.length() == 5) {
                                return null;
                            }
                        } else {
                            if (a.length() == 4) {
                                return null;
                            }
                            switch (Character.toLowerCase(a.charAt(4))) {
                                case 'q' -> promotionPiece = new Dame(getFigAtPos(origin).getFarbe());
                                case 'n' -> promotionPiece = new Springer(getFigAtPos(origin).getFarbe());
                                case 'b' -> promotionPiece = new Laeufer(getFigAtPos(origin).getFarbe());
                                case 'r' -> promotionPiece = new Turm(getFigAtPos(origin).getFarbe());
                                default -> {
                                    return null;
                                }
                            }
                            FullMove temp = new FullMove(origin, move, this, a.charAt(4) + "");
                            moveRecord.add(temp);
                            return temp;
                        }
                        FullMove temp = new FullMove(origin, move, this);
                        moveRecord.add(temp);
                        return temp;
                    }
                }
            }
        }
        return null;
    }

    public boolean move(FullMove a) {
        return move(a.getPos(), a.getMov());
    }

    public boolean move(AbsPosition a, Move b) {
        updateCastlingRights();
        if (isValidMove(new FullMove(a, b, this))) {
            updateFiftyMoveRule(a, b);
            if (getFigAtPos(a) instanceof Koenig && Math.abs(b.getxOffset()) == 2) {
                if(castlingIsPossible(a,b)) {
                    resetEnPassant();
                    if (getFigAtPos(a).getFarbe() == -1) {
                        moveCount++;
                    }
                    return castle(a,b);
                }
            } else {
                if (getFigAtPos(a) instanceof Bauer && getFigAtPos(a.addMove(b)) == null && b.getxOffset() != 0) { //checks if the move is en passant
                    setFigAtPos(a.addMove(new Move(b.getxOffset(), 0)), null); //delets pawn that is killed with en passant
                }
                if (!(getFigAtPos(a) instanceof Bauer && Math.abs(b.getyOffset()) == 2)) { //resets enPassant
                    resetEnPassant();
                } else {
                    setEnPassant(a.addMove(new Move(0,playerTurn)));
                }
                if (getFigAtPos(a) instanceof Bauer && (a.addMove(b).getY() == 7 || a.addMove(b).getY() == 0)) { //replaces the pawn with the current promotionpiece
                    setFigAtPos(a, promotionPiece);
                }
                if (getFigAtPos(a).getFarbe() == -1) {
                    moveCount++;
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

    private void updateCastlingRights() {
        if (KWCastling) {
            KWCastling = kingSideCastlingRight(1);
            getFigAtPos(getKingPos(1)).getMoveSet().getMovePattern().remove(new Move(2,0));
        }
        if (KBCastling) {
            KBCastling = kingSideCastlingRight(-1);
        } else if(kingSideCastlingRight(-1)){
            getFigAtPos(getKingPos(-1)).getMoveSet().getMovePattern().remove(new Move(2,0));
        }
        if (QWCastling) {
            QWCastling = queenSideCastlingRight(1);
        } else if(queenSideCastlingRight(1)){
            getFigAtPos(getKingPos(1)).getMoveSet().getMovePattern().remove(new Move(-2,0));
        }
        if (QBCastling) {
            QBCastling = queenSideCastlingRight(-1);
        } else if(queenSideCastlingRight(-1)){
            getFigAtPos(getKingPos(-1)).getMoveSet().getMovePattern().remove(new Move(-2,0));
        }
    }

    private void updateFiftyMoveRule(AbsPosition a, Move b) {
        if (getFigAtPos(a) instanceof Bauer || getFigAtPos(a.addMove(b)) != null) {
            fiftyMoveRule = 0;
        }
        if (getFigAtPos(a).getFarbe() == -1 && !(getFigAtPos(a) instanceof Bauer)) {
            fiftyMoveRule++;
        }
    }

    private boolean castlingIsPossible(AbsPosition a, Move b){
        if (b.getxOffset() > 0) {
            if(a.getY()==7){
                return kingSideCastlePossible(-1);
            } else {
                return kingSideCastlePossible(1);
            }
        } else {
            if(a.getY()==7){
                return queenSideCastlePossible(-1);
            } else {
                return queenSideCastlePossible(1);
            }
        }
    }

    private boolean castle(AbsPosition a, Move b) {
        int rookXPos = 0;
        if(b.getxOffset() >0 ){
            rookXPos = 7;
        }
        setFigAtPos(a.addMove(b), getFigAtPos(a));
        setFigAtPos(a, null);
        getFigAtPos(a.addMove(b)).moved();
        setFigAtPos(a.addMove(new Move(b.getxOffset() / 2, 0)), getFigAtPos(new AbsPosition(rookXPos, a.getY())));
        setFigAtPos(new AbsPosition(rookXPos, a.getY()), null);
        playerTurn = -playerTurn;
        return true;
    }

    private boolean queenSideCastlingRight(int color){
        if (color == 1) {
            color = 0;
        } else {
            color = 7;
        }
        return !kinghasMoved(color) && !rookHasMoved(color, 0);
    }

    private boolean queenSideCastlePossible(int color) {
        if (color == 1) {
            color = 0;
        } else {
            color = 7;
        }
        if (!kinghasMoved(color) && !rookHasMoved(color, 0) && !horizontalStripHasFigur(1, 3, color)) {
            ArrayList<AbsPosition> a = checker.computeMoves(new AbsPosition(4, color));
            if (a.contains(new AbsPosition(3, color))) {
                if (color == 0) {
                    return QWCastling;
                } else {
                    return QBCastling;
                }
            }
        }
        return false;
    }

    private boolean kingSideCastlingRight(int color){
        if (color == 1) {
            color = 0;
        } else {
            color = 7;
        }
        return !kinghasMoved(color) && !rookHasMoved(color, 1);
    }

    private boolean kingSideCastlePossible(int color) {
        if (color == 1) {
            color = 0;
        } else {
            color = 7;
        }
        if (!kinghasMoved(color) && !rookHasMoved(color, 1) && !horizontalStripHasFigur(5, 6, color)) {
            if(!isInCheckAfterMove(new FullMove(new AbsPosition(4,color),new Move(1,0),this)) || !isInCheckAfterMove(new FullMove(new AbsPosition(4,color),new Move(2,0),this))){
                if (color == 0) {
                    return KWCastling;
                } else {
                    return KBCastling;
                }
            }
        }
        return false;
    }

    private boolean horizontalStripHasFigur(int x1, int x2, int y) {
        for (int i = x1; i <= x2; i++) {
            if (feld[i][y].hasFigur()) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidMove(FullMove move) {
        return isValidMove(move,null);
    }

    private boolean isValidMove(FullMove move, ArrayList<AbsPosition> checkerresult){
        if(checkerresult == null){
            checkerresult = checker.computeMoves(move.getPos());
        }
        if(checkerresult.contains(move.getPos().addMove(move.getMov()))){
            if (getFigAtPos(move.getPos()).getFarbe() == playerTurn) {
                if (!isInCheckAfterMove(move)) {
                    if(getFigAtPos(move.getPos())instanceof Koenig && Math.abs(move.getMov().getxOffset()) == 2){
                        return castlingIsPossible(move.getPos(), move.getMov());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInCheckAfterMove(FullMove fullMove) {
        Feld test = copyFeld();
        int color = test.getFigAtPos(fullMove.getPos()).getFarbe();
        test.noTestMove(fullMove);
        return test.isInCheck(color);
    }

    private void noTestMove(FullMove fullMove) { //moves a figure with almost no checks attached for simulating if king is in check after own move (illegal)
        setFigAtPos(fullMove.getPos().addMove(fullMove.getMov()), getFigAtPos(fullMove.getPos()));
        setFigAtPos(fullMove.getPos(), null);
    }

    private boolean rookHasMoved(int color, int side) { //side 0 is queenside, side 1 is kingside
        if (side == 1) {
            side = 7;
        }
        if (feld[side][color].hasFigur()) {
            return feld[side][color].getFigur().getHasMoved() || !(feld[side][color].getFigur() instanceof Turm);
        }
        return true;
    }

    private boolean kinghasMoved(int color) {
        if (feld[4][color].hasFigur()) {
            if (feld[4][color].getFigur() instanceof Koenig) {
                return feld[4][color].getFigur().getHasMoved();
            }
        }
        return true;
    }

    public ArrayList<FullMove> getMoveRecord() {
        return moveRecord;
    }

    private ArrayList<AbsPosition> getAllTheoreticallyPossibleMoves(int color) {
        ArrayList<AbsPosition> res = new ArrayList<AbsPosition>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (feld[x][y].hasFigur()) {
                    if (feld[x][y].getFigur().getFarbe() == color) {
                        res.addAll(checker.computeMoves(new AbsPosition(x, y)));
                    }
                }
            }
        }
        return res;
    }

    public ArrayList<FullMove> getAllActuallyPossibleMoves(int color) {
        ArrayList<FullMove> res = new ArrayList<FullMove>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (feld[x][y].hasFigur()) {
                    if (feld[x][y].getFigur().getFarbe() == color) {
                        ArrayList<AbsPosition> temp = checker.computeMoves(new AbsPosition(x, y));
                        for (AbsPosition box : temp) {
                            FullMove temp2 = new FullMove(new AbsPosition(x, y), new Move(new AbsPosition(x, y), box), this);
                            if (isValidMove(temp2,temp)) {
                                res.add(temp2);
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    public Figur getFigAtPos(AbsPosition pos) {
        return feld[pos.getX()][pos.getY()].getFigur();
    }

    private void setFigAtPos(AbsPosition pos, Figur fig) {
        feld[pos.getX()][pos.getY()].addFigur(fig);
    }

    public AbsPosition getEnPassant() {
        return enPassant;
    }

    private void setEnPassant(AbsPosition enPassant) {
        this.enPassant = enPassant;
    }

    private void resetEnPassant() {
        this.enPassant = null;
    }

    private AbsPosition getKingPos(int color) {
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
    public String viewFrom(int color) {
        if (color == WEISS) {
            return toString();
        } else {
            StringBuilder board = new StringBuilder();
            board.append("\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557\n");
            for (int rank = 0; rank < 8; rank++) {
                board.append("\u2551");
                for (int file = 7; file >= 0; file--) {


                    if (file == 0) { // letzter File (für Rand)
                        if (feld[file][rank].hasFigur()) {
                            board.append(feld[file][rank].getFigur().toString()).append("\u2551");
                        } else {
                            if ((file + rank) % 2 == 0) { //  schwarz
                                board.append("\u2002\u2002\u2551");
                            } else { // // weiß
                                board.append("\u2002\u2002\u2551");
                            }
                        }
                    } else { // sonst
                        if (feld[file][rank].hasFigur()) {
                            board.append(feld[file][rank].getFigur().toString()).append("|");
                        } else {
                            if ((file + rank) % 2 == 0) { //  schwarz
                                board.append("\u2002\u2002|");
                            } else { // weiß
                                board.append("\u2002\u2002|");
                            }
                        }
                    }
                }
                board.append(" ").append(rank + 1).append("\n");

            }
            board.append("\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D\n\u2002\u2002h\u2002\u2002g\u2002\u2002f\u2002\u2002e\u2002\u2002d\u2002\u2002c\u2002\u2002b\u2002\u2002a");
            return board.toString();
        }
    }

    public String toFen() {
        String res = "";
        // board
        int temp = 0;
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                if (feld[x][y].getFigur() == null) {
                    temp++;
                } else {
                    if (temp != 0) {
                        res += temp;
                        temp = 0;
                    }
                    res += feld[x][y].getFigur().toLetter();
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
        // turn
        res += " " + (playerTurn == 1 ? "w" : "b");
        // castling rights
        if (!KWCastling && !QWCastling && !KBCastling && !QBCastling) {
            res += " -";
        } else {
            res += " " + (KWCastling ? "K" : "") + (QWCastling ? "Q" : "") + (KBCastling ? "k" : "") + (QBCastling ? "q" : "");
        }
        // en passant
        res += " " + (enPassant == null ? "-" : enPassant.toString());
        // fifty move rule
        res += " " + fiftyMoveRule;
        // move count
        res += " " + moveCount;
        return res;
    }

    @Override
    public String toString() {
        StringBuilder board = new StringBuilder();
        board.append("\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557\n");
        for (int rank = 7; rank >= 0; rank--) {
            board.append("\u2551");
            for (int file = 0; file < 8; file++) {


                if (file == 7) { // letzter File (für Rand)
                    if (feld[file][rank].hasFigur()) {
                        board.append(feld[file][rank].getFigur().toString()).append("\u2551");
                    } else {
                        if ((file + rank) % 2 == 0) { //  schwarz
                            board.append("\u2002\u2002\u2551");
                        } else { // // weiß
                            board.append("\u2002\u2002\u2551");
                        }
                    }
                } else { // sonst
                    if (feld[file][rank].hasFigur()) {
                        board.append(feld[file][rank].getFigur().toString()).append("|");
                    } else {
                        if ((file + rank) % 2 == 0) { //  schwarz
                            board.append("\u2002\u2002|");
                        } else { // weiß
                            board.append("\u2002\u2002|");
                        }
                    }
                }
            }
            board.append(" ").append(rank + 1).append("\n");

        }
        board.append("\u255A\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255D\n\u2002\u2002a\u2002\u2002b\u2002\u2002c\u2002\u2002d\u2002\u2002e\u2002\u2002f\u2002\u2002g\u2002\u2002h");
        return board.toString();
    }
}