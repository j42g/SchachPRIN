package io.server;

public class SchachSpiel {

    private ClientHandler white;
    private ClientHandler black;

    private ClientHandler hasTurn;


    public SchachSpiel(ClientHandler a, ClientHandler b){ // a sollte der sein, der vorher gewartet hat, kann aber sein, dass das egal ist
        if(Math.random() < 0.5){ // wer ist was
            this.white = a;
            this.black = b;
        } else {
            this.white = b;
            this.black = a;
        }
        // threads informieren
        a.giveGame(this);
        b.giveGame(this);
        // TODO feld generieren
        // TODO weiß nach move fragen aka gameloop starten
    }

    public SchachSpiel(String json){

    }

    public void move(){

    }


    public void saveToFile(){

    }

}
