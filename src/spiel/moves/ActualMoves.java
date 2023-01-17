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

    public ArrayList<AbsPosition> computeMoves(AbsPosition pos) {
        return computeMovesBack(pos, false);
    }

    public ArrayList<AbsPosition> computeMovesBack(AbsPosition pos, boolean recursion) {
        if (feld.getFigAtPos(pos) == null) {
            System.out.println("no figure at requested position, computeMovesBack() error");
            return new ArrayList<AbsPosition>();
        }
        ArrayList<AbsPosition> res = new ArrayList<AbsPosition>();
        MovePattern pattern = feld.getFigAtPos(pos).getMoveSet();
        if (feld.getFigAtPos(pos) instanceof Bauer) {
            for(Move box : pattern.getMovePattern()){
                try {
                    if (box.getxOffset() != 0) {
                        if (feld.getFigAtPos(pos.addMove(box)) != null) {
                            if (feld.getFigAtPos(pos.addMove(box)).getFarbe() == -feld.getFigAtPos(pos).getFarbe()) {
                                res.add(pos.addMove(box));
                            }
                        } else if(pos.addMove(box).equals(feld.getEnPassant())){
                            res.add(pos.addMove(box));
                        }
                    } else {
                        if (feld.getFigAtPos(pos.addMove(box)) == null) {
                            if (Math.abs(box.getyOffset()) == 2) {
                                if (res.contains(new AbsPosition(pos.addMove(new Move(0, feld.getFigAtPos(pos).getFarbe()))))) {
                                    res.add(pos.addMove(box));
                                    feld.setEnPassant(new AbsPosition(pos.addMove(new Move(0, feld.getFigAtPos(pos).getFarbe()))));
                                }
                            } else {
                                res.add(pos.addMove(box));
                            }
                        }
                    }
                }catch(Exception E){

                }
            }
            return res;
        }
        if (feld.getFigAtPos(pos) instanceof Koenig && recursion) {
            if (feld.queenSideCastlePossible(feld.getFigAtPos(pos).getFarbe())) {
                pattern.addMove(-2, 0);
            } else if (feld.kingSideCastlePossible(feld.getFigAtPos(pos).getFarbe())) {
                pattern.addMove(2, 0);
            }
        }
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
                                    if(feld.getFigAtPos(res.get(res.size() - 1)).getFarbe()==feld.getFigAtPos(pos).getFarbe()){
                                        res.remove(res.size()-1);
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

        int i = 0;
        while (i != res.size()) {
            if (res.get(i).isPossible()) {
                i++;
            } else {
                res.remove(i);
            }
        }
        if (!recursion) {
            if (feld.getFigAtPos(pos) instanceof Koenig) {
                ArrayList<AbsPosition> enemyPos = feld.getAllPossibleMoves(-feld.getFigAtPos(pos).getFarbe());
                i = 0;
                while (i != res.size()) {
                    if (enemyPos.contains(res.get(i))) {
                        res.remove(res.get(i));
                    } else {
                        i++;
                    }
                }
            }
        }

        return res;
    }
}
