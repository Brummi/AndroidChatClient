package com.brummid.chatclient;


import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/*
*
* Created by Felix Wimbauer 12/10/2015
*
 */

public class ChatActivity extends AppCompatActivity {

    static ArrayList<String> messages;
    Client client;
    Connection con;
    String name, ip;
    int port;
    Handler updateConversationHandler;

    Toolbar toolbar;
    EditText messageField;
    TextView chat;
    FloatingActionButton sendButton;

    boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chat = (TextView) findViewById(R.id.chat);
        chat.setMovementMethod(new ScrollingMovementMethod());
        messageField = (EditText) findViewById(R.id.message);
        sendButton = (FloatingActionButton) findViewById(R.id.send);
        sendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(!messageField.getText().toString().equals(""))
                {
                    send(messageField.getText().toString());
                    messageField.setText("");
                }
            }
        });

        updateConversationHandler = new Handler();

        if(con == null)
        {
            if(savedInstanceState == null)messages = new ArrayList<String>();

            name = getIntent().getStringExtra("name");
            ip = getIntent().getStringExtra("ip");
            String portS = getIntent().getStringExtra("port");
            int port = Integer.parseInt(portS);

            Log.i("info", name + " " + ip + " " + port);

            con = new Connection(ip, port);
            con.start();
        }

    }

    public void send(String message)
    {
        client.send(message);
    }

    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Disconnected",Toast.LENGTH_LONG).show();
        try
        {
            client.close();
            con.stop();

        }catch(Exception e){}
        client = null;
        con = null;
        finish();
        return;
    }

    public void onStop()
    {
        super.onStop();
        try
        {
            active = false;
            client.close();
        }catch(Exception e){}
        client = null;
        con = null;

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putBoolean("connected", true);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private class Connection extends Thread
    {
        private String ip;
        private int port;

        private BufferedReader reader;

        public Connection(String ip, int port)
        {
            this.ip = ip;
            this.port = port;
        }

        public void run()
        {
            Log.i("info", "Con thread started");
            client = new Client(ip, port);

            if(!client.connected)
            {
//	        	Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_LONG).show();
                updateConversationHandler.post(new Error());
                return;
            }

            client.send(name);

            reader = client.getReader();

            try {
                if(!reader.readLine().contains("ok"))
                {
                    updateConversationHandler.post(new NameinUse());
                    client.close();
                    return;
                }
            } catch (IOException e) {
                updateConversationHandler.post(new Error());
                client.close();
                return;
            }

            updateConversationHandler.post(new UpdateTitle());

            updateConversationHandler.post(new Refresh());

            String s = null;

            active = true;

            while(active)
            {
                try {
                    if((s = reader.readLine()) != null)
                    {
                        messages.add(s);
                        updateConversationHandler.post(new Refresh());
                    };
                } catch (IOException e)
                {
                    messages.add("Server> closed");
                    active = false;
                }
            }
        }
    }

    private class Refresh implements Runnable
    {

        @Override
        public void run()
        {
            String text = "";

            for(String s: messages)text += s + "\n";

            if(!text.equals(chat.getText().toString()))
            {
                chat.setText(text);
            }
        }

    }

    private class UpdateTitle implements Runnable
    {
        public void run()
        {
            toolbar.setTitle("Chat: " + name + " @ " + client.getSocket().getInetAddress());

            Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_LONG).show();
        }
    }

    private class Error implements Runnable
    {
        public void run()
        {
            toolbar.setTitle("Connection failed");

            Toast.makeText(getApplicationContext(),"Connection Error",Toast.LENGTH_LONG).show();
        }
    }

    private class NameinUse implements Runnable
    {
        public void run()
        {
            toolbar.setTitle("Username already in use!");

            Toast.makeText(getApplicationContext(),"Username already in use!",Toast.LENGTH_LONG).show();
        }
    }

}
