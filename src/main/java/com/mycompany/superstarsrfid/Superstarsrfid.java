/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.superstarsrfid;

import com.caen.RFIDLibrary.CAENRFIDTag;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

    // this might need to be tweaked a bit
    private static final long TAG_EXPIRATION_TIME_MS = 2000;
    private static final Map<String, Long> masterTagList = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        RFIDReader reader = new RFIDReader();
        WebsocketClientEndpoint client = new WebsocketClientEndpoint();
        client.connect("ws://localhost:8080"); // Replace with your server URL if required!

        // Background thread to clean up stale tags
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(Superstarsrfid::removeStaleTags, 500, 500, TimeUnit.MILLISECONDS);

        reader.setOnTagsDetectedListener(tags -> {
            if (tags != null && client.isConnected()) {
                long currentTime = System.currentTimeMillis();

                for (CAENRFIDTag tag : tags) {
                    String tagId = Base64.getEncoder().encodeToString(tag.GetId());
                    masterTagList.put(tagId, currentTime); // Refresh timestamp
                }
                
                sendUpdatedTags(client);
            }
        });

        reader.start();
    }

    private static void removeStaleTags() {
        long currentTime = System.currentTimeMillis();
        masterTagList.entrySet().removeIf(entry -> currentTime - entry.getValue() > TAG_EXPIRATION_TIME_MS);
    }

    private static void sendUpdatedTags(WebsocketClientEndpoint client) {
        List<String> stableTags = new ArrayList<>(masterTagList.keySet());

        JSONObject jsonTagsList = new JSONObject();
        jsonTagsList.put("tags", new JSONArray(stableTags));
        client.sendMessage(jsonTagsList.toString());
    }
}
