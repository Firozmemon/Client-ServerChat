package com.firozmemon.client_serverchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

public class ReadServerIPActivity extends AppCompatActivity {

    EditText ipaddress;
    Button connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_server_ip);

        ipaddress = (EditText) findViewById(R.id.ipaddress);
        connect = (Button) findViewById(R.id.connect);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ipaddress.getText().toString();
                if ((Patterns.IP_ADDRESS).matcher(ip).matches()) {
                    //Toast.makeText(ReadServerIPActivity.this, "Valid IpAddress", Toast.LENGTH_LONG).show();
                    new EstablishConnection(ip).execute();
                } else {
                    Toast.makeText(ReadServerIPActivity.this, "Invalid IpAddress", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class EstablishConnection extends AsyncTask<Void, Void, Boolean> {
        String ip;
        Client client = new Client();

        public EstablishConnection(String ip) {
            this.ip = ip;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Boolean connectionEstablished = false;

            client.start();
            try {
                client.connect(9000, ip, 12345);
                connectionEstablished = true;
            } catch (IOException e) {
                e.printStackTrace();
                connectionEstablished = false;
            }
            return connectionEstablished;
        }

        @Override
        protected void onPostExecute(Boolean connectionEstablished) {
            if (connectionEstablished.booleanValue()) {
                App.client = client;
                Toast.makeText(ReadServerIPActivity.this, "connectionEstablished", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ReadServerIPActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ReadServerIPActivity.this, "NOconnectionEstablished", Toast.LENGTH_LONG).show();
            }
        }
    }
}
