import client.SchachClient;
import server.SchachServer;
import spiel.figur.*;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random r = new Random();
        System.out.println(r.nextInt());

        SchachServer server = new SchachServer();
        SchachClient client = new SchachClient();

        Thread threadServer = new Thread(server);
        Thread threadClient = new Thread(client);
        threadServer.start();
        threadClient.start();


        System.out.println("\u265F");




    }
}