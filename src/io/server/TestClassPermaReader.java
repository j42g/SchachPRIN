package io.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TestClassPermaReader {

    public static void main(String[] args){
        Socket client = null;
        try (ServerSocket server = new ServerSocket(7777)) {
            while (true) {
                client = server.accept();
                System.out.println("Verbunden mit" + client.toString());
                break;
            }
        } catch (Exception e) {}

        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream())){
            while(true){
                System.out.println(in.readLine());
            }
        } catch (Exception e) {}

    }

}
