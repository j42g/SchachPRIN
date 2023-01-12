package spiel.moves;

import spiel.feld.Feld;

import java.util.ArrayList;

public class ActualMoves {
    private Feld feld;


    public ActualMoves(Feld feld) {
        this.feld = feld;

    }


    public ArrayList<AbsPosition> computeMoves(AbsPosition pos) {
        MovePattern pattern = feld.getFigAtPos(pos).getMoveSet();
        ArrayList<AbsPosition> res = new ArrayList<AbsPosition>();
        for (Move box : pattern.getMovePattern()) {
            try {
                res.add(pos.addMove(box));
                if (feld.getFigAtPos(pos.addMove(box)) != null) {
                    if (feld.getFigAtPos(pos.addMove(box)).getFarbe() == feld.getFigAtPos(pos).getFarbe()) {
                        res.remove(res.size() - 1);
                    }
                } else {
                    if (box.isBlockable()) {
                        for (int i = 1; i < 8; i++) {
                            try {
                                if (feld.getFigAtPos(res.get(res.size() - 1)) != null) {
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
        int i = 0;
        while (i != res.size()) {
            if (res.get(i).isPossible()) {
                i++;
            } else {
                res.remove(i);
            }
        }
        return res;
    }
}
