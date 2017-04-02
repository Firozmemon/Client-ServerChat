package com.firozmemon.client_serverchat;

import android.app.Application;

import com.esotericsoftware.kryonet.Client;

/**
 * Created by Firoz.
 */

public class App extends Application {
    public static Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
