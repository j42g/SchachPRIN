package spiel.moves;

public class AbsPosition {
    private int x;
    private int y;

    public AbsPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public AbsPosition(String square) {
        if (square.length() == 2) {
            if("abcdefgh".indexOf(square.charAt(0)) != -1 && "12345678".indexOf(square.charAt(1)) != -1) { // korrektes Format
                this.x = square.charAt(0) - 0x61;
                this.y = square.charAt(1) - 0x31;
                System.out.println(x + ", " + y);
            } else {
                System.out.println("Falsches Format");
            }
        } else {
            System.out.println("Falsches Format");
        }
    }

    public AbsPosition addMove(Move move) {
        return new AbsPosition(this.x + move.getxOffset(), this.y + move.getyOffset());
    }

    public boolean isPossible() {
        return !(x < 0 || x > 7 || y < 0 || y > 7);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        String AbcPos = "";
        String NPos = "";
        switch (x) {
            case 0 -> AbcPos = "a";
            case 1 -> AbcPos = "b";
            case 2 -> AbcPos = "c";
            case 3 -> AbcPos = "d";
            case 4 -> AbcPos = "e";
            case 5 -> AbcPos = "f";
            case 6 -> AbcPos = "g";
            case 7 -> AbcPos = "h";
        }
        switch (y) {
            case 0 -> NPos = "1";
            case 1 -> NPos = "2";
            case 2 -> NPos = "3";
            case 3 -> NPos = "4";
            case 4 -> NPos = "5";
            case 5 -> NPos = "6";
            case 6 -> NPos = "7";
            case 7 -> NPos = "8";
        }

        return AbcPos + NPos;
    }

    public boolean equals(Object o) {
        if (o instanceof AbsPosition) {
            AbsPosition a = (AbsPosition) o;
            if (a.getX() == this.x && a.getY() == this.y) {
                return true;
            }
        }
        return false;
    }
}
