package com.firozmemon.client_serverchat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.firozmemon.client_serverchat.model.SomeRequest;
import com.firozmemon.client_serverchat.model.SomeResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText messageEditText;
    Button sendButton;
    ListView messageList;

    Client client;

    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageList = (ListView) findViewById(R.id.messageList);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendButton = (Button) findViewById(R.id.sendButton);

        client = App.client;

        adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,list);
        messageList.setAdapter(adapter);
        if (list != null && list.size() > 0)
        {
            messageList.setSelection(list.size() - 1);
        }


        if (client.isConnected()) {

            Kryo kryo = client.getKryo();
            kryo.register(SomeRequest.class);
            kryo.register(SomeResponse.class);

            client.addListener(new Listener() {
                @Override
                public void disconnected(Connection connection) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                client.start();
                                client.reconnect();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }

                @Override
                public void received(Connection connection, Object object) {
                    if (object instanceof SomeResponse) {
                        SomeResponse response = (SomeResponse) object;
                        try {
                            response.text = URLDecoder.decode(response.text, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        list.add(response.text);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (adapter!=null){
                                    adapter.notifyDataSetChanged();
                                    if (list != null && list.size() > 0)
                                    {
                                        messageList.setSelection(list.size() - 1);
                                    }
                                }else{
                                    adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,list);
                                    messageList.setAdapter(adapter);
                                    if (list != null && list.size() > 0)
                                    {
                                        messageList.setSelection(list.size() - 1);
                                    }
                                }
                            }
                        });
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "NOT Connected", Toast.LENGTH_LONG).show();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (client.isConnected()) {
                    String message = messageEditText.getText().toString();
                    message = "Client:"+message;

                    new SendMessage(message).execute();
                } else {
                    Toast.makeText(MainActivity.this, "Cannot send message. Reason: not connected", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private class SendMessage extends AsyncTask<Void, Void, Boolean> {
        String message;

        public SendMessage(String message) {
            this.message = message;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Boolean messageSent = false;

            try {
                SomeRequest request = new SomeRequest();
                request.text = message;
                client.sendTCP(request);
                messageSent = true;
            } catch (Exception e) {
                e.printStackTrace();
                messageSent = false;
            }

            return messageSent;
        }

        @Override
        protected void onPostExecute(Boolean messageSent) {
            if (messageSent.booleanValue()) {
                messageEditText.setText("");
                list.add(message);
                if (adapter!=null){
                    adapter.notifyDataSetChanged();
                    if (list != null && list.size() > 0)
                    {
                        messageList.setSelection(list.size() - 1);
                    }
                }else{
                    adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,list);
                    messageList.setAdapter(adapter);
                    if (list != null && list.size() > 0)
                    {
                        messageList.setSelection(list.size() - 1);
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "message not sent", Toast.LENGTH_LONG).show();
            }
        }
    }
}
