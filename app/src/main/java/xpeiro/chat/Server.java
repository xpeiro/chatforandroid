package xpeiro.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Server extends Thread {

    private Socket socket = null;
    private MulticastSocket mcsocket = null;
    private int port;
    private String clientusername;
    private Hashtable<String, String> userhash;
    InetAddress group;
    BufferedReader in;


    public Server(Socket socket, Hashtable<String, String> userhash) {
        super("Server");
        this.socket = socket;
        this.port = socket.getLocalPort();
        this.userhash = userhash;
        try {
            this.mcsocket = new MulticastSocket(port);
        } catch (Exception e) {
            System.err.println("error");
        }

    }


    public void loginclient() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String password;
            //get username from client
            String username = in.readLine();
            //check if given username is not empty
            if (!username.isEmpty()) {
                out.println("usuariodado");
                //check for username in userhash
                if (userhash.get(username) != null) {
                    out.println("User " + username + " exists. Enter Password: ");
                    password = in.readLine();
                    if (password.equals(userhash.get(username))) {
                        out.println("Correct Password");
                        clientusername = username;
                    } else {
                        System.out.println("Incorrect Password");
                        out.println("Incorrect Password. Using IP as username");
                    }
                } else {
                    out.println("New User. Enter new password to register: ");
                    password = in.readLine();
                    userhash.put(username, password);
                    clientusername = username;
                    out.println("New User Registered.\n");
                }
            } else {
                out.println("usuariovacio");
                out.println("Username not given, using IP as Username\n");
            }

        } catch (IOException e) {
            System.err.println("login error");

            e.printStackTrace();
        }
    }


    private void send(String message) {
        byte[] dgbuf = message.getBytes();
        DatagramPacket dgpaquete = new DatagramPacket(dgbuf, dgbuf.length, group, port);
        try {
            mcsocket.send(dgpaquete);
        } catch (Exception e) {
            System.err.println("error sending on multicast");

        }
    }

    @Override
    public void run() {

        clientusername = socket.getInetAddress().getHostAddress();
        String clientin;
        try {
            group = InetAddress.getByName("224.0.0.1");
        } catch (Exception e) {
            System.err.println("error: nonexistng group");

        }

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e1) {
            System.err.println("error configuring IO");
        }
        try {
            loginclient();

            System.out.println(clientusername + " connected.");
            send("\n");
            send(clientusername + " connected.");
            send("\n");


            while ((clientin = in.readLine()) != null) {
                clientin = clientusername + ": " + clientin;
                send(clientin);
            }


        } catch (Exception e) {

            System.err.println(clientusername + " disconnected.");
            send("\n");
            send(clientusername + " disconnected.");
            send("\n");
            mcsocket.close();
        }
    }
}
