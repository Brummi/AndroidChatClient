package com.brummid.chatclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

/*
*
* Created by Felix Wimbauer 12/10/2015
*
 */

public class MainActivity extends AppCompatActivity {

    EditText nameText, ipText, portText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.title_activity_main);

        nameText = (EditText) findViewById(R.id.nameField);
        ipText = (EditText) findViewById(R.id.ipField);
        portText = (EditText) findViewById(R.id.portField);
    }

    public void connect(View v) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("name", nameText.getText().toString());
        intent.putExtra("ip", ipText.getText().toString());
        intent.putExtra("port", portText.getText().toString());
        startActivity(intent);
    }

}