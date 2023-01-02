import io.client.Client;
import io.server.ServerVerwaltung;

public class Main {

    public static void main(String[] args) {

        ServerVerwaltung server = new ServerVerwaltung();
        Client client = new Client();

        Thread serverThread = new Thread(server);
        Thread clientThread = new Thread(client);

        serverThread.start();
        clientThread.start();

    }

}