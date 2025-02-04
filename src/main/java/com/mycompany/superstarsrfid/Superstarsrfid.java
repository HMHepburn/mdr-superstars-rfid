/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.superstarsrfid;

import com.caen.RFIDLibrary.CAENRFIDTag;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author hanna
 */

// Entry point of the application
// Initialize modules and starts the application!


//INSTRUCTIONS:
//In RFIDReader.java - make sure to change the COM value in the start() function to match
//the USB port that the RFID reader is plugged into
public class Superstarsrfid {

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        
        RFIDReader reader = new RFIDReader();
        
        WebsocketClientEndpoint client = new WebsocketClientEndpoint();
        client.connect("ws://localhost:8080"); // Replace with your server URL
        
        reader.setOnTagsDetectedListener(tags -> {
            if(tags != null && client.isConnected()) {
                // create the array list of tags
                // might modify this to simplify?
                List<String> tagList = new ArrayList<>();
                for (CAENRFIDTag tag : tags) {
                    tagList.add(new String(tag.GetId()));
                }
                
                // Construct JSON payload
                JSONObject jsonTagsList = new JSONObject();
                jsonTagsList.put("type", "tag_detection");
                jsonTagsList.put("tags", new JSONArray(tagList));
                
                client.sendMessage(jsonTagsList.toString());
            }
        });
        
        reader.start();
        
        try {
            while (true) {
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
