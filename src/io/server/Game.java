package io.server;

public class Game {

    private ClientHandler white;
    private ClientHandler black;

    private ClientHandler hasTurn;


    public Game(ClientHandler white, ClientHandler black){
        this.white = white;
        this.black = black;
    }

    public Game(String json){

    }

    public void move(){

    }


    public void saveToFile(){

    }

}
