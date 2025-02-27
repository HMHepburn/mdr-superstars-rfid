/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.superstarsrfid;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import java.net.URI;
import org.json.JSONObject;  // You'll need to add org.json dependency

/**
 *
 * @author hanna
 */

@ClientEndpoint
public class WebsocketClientEndpoint {
    private Session session;
    private boolean isConnected = false;

    public void connect(String uri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, URI.create(uri));
            this.isConnected = true;
            //System.out.println("Connected to server: " + uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected and ready");
        
        JSONObject testMessage = new JSONObject()
                .put("type", "message")
                .put("value", "Hello from Java client!");
        sendMessage(testMessage.toString());
    }

    @OnMessage
    public void onMessage(String message) {
        //System.out.println("Received raw message: " + message);
        
        try {
            JSONObject json = new JSONObject(message);
            String command = json.getString("command");

            switch (command) {
                case "START_DETECTION":
                    System.out.println("STARTING DETECTION");
                    //startDetection();
                    break;
                case "STOP_DETECTION":
                    System.out.println("STOPPING DETECTION");
                    //stopDetection();
                    break;
                default:
                    System.out.println("Unknown command: " + command);
            }
        } catch (Exception e) {
            sendError("Error processing command: " + e.getMessage());
        }
    }
    
    private void sendError(String error) {
        JSONObject json = new JSONObject()
            .put("type", "error")
            .put("value", error);
        //sendMessage(json.toString());
        System.out.println(error);
    }

    public void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
            System.out.println("Sent message: " + message);
        } else {
            System.out.println("Unable to send message, session is not open.");
        }
    }
    
    public boolean isConnected() {
        return isConnected && session != null && session.isOpen();
    }
}
