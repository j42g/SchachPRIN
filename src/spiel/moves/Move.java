package spiel.moves;

public class Move {
    private int xOffset;
    private int yOffset;
    private boolean blockable;


    public Move(int xOffset, int yOffset){
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.blockable = false;

    }

    public Move(int xOffset, int yOffset, boolean blockable) {
        this(xOffset, yOffset);
        this.blockable = blockable;
    }

    public Move(AbsPosition origin, AbsPosition destination) {
        this.xOffset = destination.getX() - origin.getX();
        this.yOffset = destination.getY() - origin.getY();
    }


    public int getxOffset() {
        return xOffset;
    }

    public int getyOffset() {
        return yOffset;
    }

    public boolean isBlockable() {
        return blockable;
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof Move) {
            Move a = (Move) o;
            if (a.getxOffset() == this.xOffset && a.getyOffset() == this.yOffset) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return "xOffset: " + xOffset + "; yOffset: " + yOffset;
    }
}
