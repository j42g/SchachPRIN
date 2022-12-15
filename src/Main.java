import client.SchachClient;
import server.SchachServer;
import spiel.K;
import spiel.figur.*;

import java.util.Random;

public class Main {
    public static void main(String[] args) {

        SchachServer server = new SchachServer();
        SchachClient client = new SchachClient();

        Thread threadServer = new Thread(server);
        Thread threadClient = new Thread(client);
        threadServer.start();
        threadClient.start();





    }
}