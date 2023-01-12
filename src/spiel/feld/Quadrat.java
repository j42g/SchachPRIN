package spiel.feld;
import spiel.figur.*;

public class Quadrat {


    private Figur docker = null;
    public Quadrat(){
    }
    public Quadrat(Figur figur){
        this.docker = figur;
    }
    public void addFigur(Figur figur){
        this.docker = figur;
    }
    public Figur getFigur(){
        return this.docker;
    }
    public boolean hasFigur(){
        return this.docker != null;
    }
    public String toString(){
        if(docker == null){
            return " ";
        } else {
            return this.docker.toString();
        }
    }

}
