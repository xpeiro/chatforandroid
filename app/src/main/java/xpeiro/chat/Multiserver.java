package xpeiro.chat;

import java.net.ServerSocket;
import java.util.Hashtable;

public class Multiserver extends Thread {

    int port;
    Hashtable<String, String> userhash = new Hashtable<String, String>();


    public Multiserver() {
        port = 8080;
    }


    public Multiserver(int port) {
        this.port = port;
    }


    @Override
    public void run() {
        userhash.put("dani", "1234");
        // makes new connection and starts a server for each one.
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Waiting for clients on port: " + port + "...");
            while (true) {
                new Server(serverSocket.accept(), userhash).start();
            }

        } catch (Exception e) {
            System.err.println("error accepting new connnection (port " + port + " unavailable?)");
            System.exit(1);
        }
    }
}
