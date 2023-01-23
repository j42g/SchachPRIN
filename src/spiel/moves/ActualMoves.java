package spiel.moves;

import spiel.feld.Feld;
import spiel.figur.Bauer;
import spiel.figur.Koenig;

import java.util.ArrayList;

public class ActualMoves {
    private Feld feld;


    public ActualMoves(Feld feld) {
        this.feld = feld;
    }


    public ArrayList<AbsPosition> computeMoves(AbsPosition pos) { //exceptions for king movement are handled elsewhere and thus not mentioned here
        if (feld.getFigAtPos(pos) == null) {
            return new ArrayList<AbsPosition>();
        }
        ArrayList<AbsPosition> res;
        if (feld.getFigAtPos(pos) instanceof Bauer) {
            res = calcBauernMoves(pos);
        } else {
            res = calcNormalMoves(pos);
        }
        removeImpossiblePositions(res);
        return res;
    }

    private ArrayList<AbsPosition> calcNormalMoves(AbsPosition pos) {
        ArrayList<AbsPosition> res = new ArrayList<AbsPosition>();
        MovePattern pattern = feld.getFigAtPos(pos).getMoveSet();
        for (Move box : pattern.getMovePattern()) {
            try {
                res.add(pos.addMove(box)); //assumes that move is possible and adds it
                if (feld.getFigAtPos(pos.addMove(box)) != null) {
                    if (feld.getFigAtPos(pos.addMove(box)).getFarbe() == feld.getFigAtPos(pos).getFarbe()) {
                        res.remove(res.size() - 1);   //removes the last move if there is a piece of the same color on the end position
                    }
                } else { //if square in current direction is not blockable and move is raymove (blockable) continues with the ray
                    if (box.isBlockable()) {
                        for (int i = 1; i < 8; i++) {
                            try {
                                if (feld.getFigAtPos(res.get(res.size() - 1)) != null) { //proceeds until there is no piece where the current piece would move
                                    if (feld.getFigAtPos(res.get(res.size() - 1)).getFarbe() == feld.getFigAtPos(pos).getFarbe()) { //end of the loop
                                        res.remove(res.size() - 1);
                                    }
                                    break;
                                } else {
                                    res.add(res.get(res.size() - 1).addMove(box));
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            } catch (Exception e) {
            }

        }
        return res;
    }

    private ArrayList<AbsPosition> calcBauernMoves(AbsPosition pos) {
        ArrayList<AbsPosition> res = new ArrayList<AbsPosition>();
        MovePattern pattern = feld.getFigAtPos(pos).getMoveSet();
        for (Move box : pattern.getMovePattern()) {
            try {
                if (box.getxOffset() != 0) {
                    if (feld.getFigAtPos(pos.addMove(box)) != null) {
                        if (feld.getFigAtPos(pos.addMove(box)).getFarbe() == -feld.getFigAtPos(pos).getFarbe()) {
                            res.add(pos.addMove(box));
                        }
                    } else if (pos.addMove(box).equals(feld.getEnPassant())) {
                        res.add(pos.addMove(box));
                    }
                } else {
                    if (feld.getFigAtPos(pos.addMove(box)) == null) {
                        if (Math.abs(box.getyOffset()) == 2) {
                            if (res.contains(new AbsPosition(pos.addMove(new Move(0, feld.getFigAtPos(pos).getFarbe()))))) {
                                res.add(pos.addMove(box));
                            }
                        } else {
                            res.add(pos.addMove(box));
                        }
                    }
                }
            } catch (Exception E) {
            }
        }
        return res;
    }

    private void removeImpossiblePositions(ArrayList<AbsPosition> res) {
        int i = 0;
        while (i != res.size()) {
            if (res.get(i).isPossible()) {
                i++;
            } else {
                res.remove(i);
            }
        }
    }
}
