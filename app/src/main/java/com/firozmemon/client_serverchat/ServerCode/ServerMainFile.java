package com.firozmemon.client_serverchat.ServerCode;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.firozmemon.client_serverchat.model.SomeRequest;
import com.firozmemon.client_serverchat.model.SomeResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

/**
 * Created by Firoz.
 */

public class ServerMainFile {

    public static void main(String[] args) throws IOException{
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        final Server server = new Server();
        server.start();
        server.bind(12345); //throws IOException

        Kryo kryo = server.getKryo();
        kryo.register(SomeRequest.class);
        kryo.register(SomeResponse.class);

        server.addListener(new Listener(){
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof SomeRequest) {
                    SomeRequest request = (SomeRequest)object;
                    System.out.println(request.text);

                    try {
                        System.out.print("Enter your Response::");
                        String responseText = br.readLine();    //Read response from server
                        responseText = URLEncoder.encode(responseText,"UTF-8");

                        SomeResponse response = new SomeResponse();
                        response.text = "Server:"+responseText;
                        connection.sendTCP(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }
}
