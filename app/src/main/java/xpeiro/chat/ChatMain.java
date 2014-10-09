package xpeiro.chat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class ChatMain extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    Client client;
    TextView output;
    EditText input;
    EditText hostedittext;
    EditText portedittext;
    EditText usernameedittext;
    String username = "";
    EditText passwordedittext;
    String password = "";
    Button serverbutton;
    Button sendbutton;
    Button clientbutton;
    int port = 8080;
    String host = "localhost";
    MulticastSocket mcsocket;
    InetAddress group;
    String fromall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        output = (TextView) findViewById(R.id.textView);
        //output.setMovementMethod(new ScrollingMovementMethod());
        output.setMaxLines(30);
        output.setTextIsSelectable(true);
        input = (EditText) findViewById(R.id.editText);
        hostedittext = (EditText) findViewById(R.id.editText2);
        portedittext = (EditText) findViewById(R.id.editText3);
        usernameedittext = (EditText) findViewById(R.id.editText4);
        passwordedittext = (EditText) findViewById(R.id.editText5);
        serverbutton = (Button) findViewById(R.id.server);
        clientbutton = (Button) findViewById(R.id.client);
        sendbutton = (Button) findViewById(R.id.send);

    }


    public void configurar() {
        try {
            mcsocket = new MulticastSocket(port);
            group = InetAddress.getByName("224.0.0.1");
            mcsocket.joinGroup(group);

        } catch (Exception e) {
            System.err.println("visor config error.");

        }
    }

    private void recieve() {

        byte[] dgbuf = new byte[256];
        //recieve datagram packet translate to string.
        DatagramPacket dgpacket = new DatagramPacket(dgbuf, dgbuf.length);
        try {
            mcsocket.receive(dgpacket);
            fromall = new String(dgpacket.getData());
            //trim excess bytes
            fromall = fromall.trim();

        } catch (Exception e) {
            System.err.println("error recieving data");

        }

    }

    // thread to recieve data from multicast.
    Runnable visor = new Runnable() {

        public void run() {
            System.out.println("Visor Open on Port: " + port);
            while (!mcsocket.isClosed()) {
                Message msg = handler.obtainMessage();
                recieve();
                System.out.println(fromall);
                // call handler with message
                msg.obj = fromall;

                handler.sendMessage(msg);
            }
            System.out.println("Visor Closed");
        }
    };

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            output.append("\n" + msg.obj);
            output.scrollTo(0, output.getBottom());
            super.handleMessage(msg);
        }
    };

    Thread visorthread = new Thread(visor);


    public void server(View view) {
        if (!portedittext.getText().toString().isEmpty()) {
            port = Integer.parseInt(portedittext.getText().toString());
        }
        new Multiserver(port).start();
        output.append("Server started on port: " + port);
        output.append("\nWaiting for clients on port: " + port + "...");
        serverbutton.setEnabled(false);

    }

    public void client(View view) {

        if (!hostedittext.getText().toString().isEmpty()) {
            host = hostedittext.getText().toString();
        }
        if (!portedittext.getText().toString().isEmpty()) {
            port = Integer.parseInt(portedittext.getText().toString());
        }
        if (!usernameedittext.getText().toString().isEmpty()) {
            username = usernameedittext.getText().toString();
        }
        if (!passwordedittext.getText().toString().isEmpty()) {
            password = passwordedittext.getText().toString();
        }

        while (visorthread.isAlive()) {
            mcsocket.close();
        }
        if (visorthread.getState() != Thread.State.NEW) {
            visorthread = new Thread(visor);
        }
        client = new Client(host, port, username, password);
        configurar();
        visorthread.start();
        try {
            client.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    public void send(View view) {

        client.send(input.getText().toString());
    }

}
