package spiel.moves;

public class Move {
    private int xOffset;
    private int yOffset;
    private boolean blockable;
    private boolean bauerMove;

    public Move(int xOffset, int yOffset){
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.blockable = false;
        this.bauerMove = false;
    }
    public Move(int xOffset, int yOffset, boolean blockable){
        this(xOffset,yOffset);
        this.blockable = blockable;
    }
    public Move(int xOffset, int yOffset, boolean blockable, boolean bauerMove){
        this(xOffset, yOffset, blockable);
        this.bauerMove = bauerMove;
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

    public boolean isBauerMove() {
        return bauerMove;
    }
    public String toString(){
        return "xOffset: "+ xOffset+"; yOffset: "+yOffset;
    }
}
