package xpeiro.chat;

/**
 * Created by xpeiro on 8/10/14.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client extends Thread {

    String host;
    int port;
    String clientusername = "";
    String clientpassword = "";
    Socket clientSocket;
    PrintWriter out;


    public Client(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.clientusername = username;
        this.clientpassword = password;
    }


    public void send(String mensaje) {
        if (mensaje != null) {
            out.println(mensaje);
        }

    }


    public void configureIO() throws IOException, UnknownHostException {
        clientSocket = new Socket(host, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);

    }


    public void userlogin() {
        try {
            //send username to server
            out.println(clientusername);
            //create reader on server socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //respond to server message
            switch (in.readLine()) {
                //case of non empty username
                case "usuariodado":
                    System.out.println(in.readLine());
                    out.println(clientpassword);
                    String passwordisaccepted = in.readLine();
                    System.out.println(passwordisaccepted);
                    break;
                //case of empty user
                case "usuariovacio":
                    String emptyuser = in.readLine();
                    System.out.println(emptyuser);
                    break;
            }

        } catch (Exception e) {
            System.err.println("login error");
        }
    }

    @Override
    public void run() {
        try {
            String input;
            //configure socket
            configureIO();
            System.out.println("Host: " + host + "\nPuerto: " + port);
            userlogin();

        } catch (Exception e) {
            System.err.println("server connection error");
        }


    }
}
