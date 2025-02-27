/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.superstarsrfid;

import com.caen.RFIDLibrary.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hanna
 */
// Handle communication with RFID reader using the SDK
// Detect tags and trigger events
public class RFIDReader {

    // Listening for event! (tag detection)
    private OnTagsDetectedListener listener;

    // Interface to describe onTagsDetectedListener variable
    public interface OnTagsDetectedListener {

        void onTagsDetected(CAENRFIDTag[] tags);
    }

    // setting global listener object to OnTagsDetectedListener object
    public void setOnTagsDetectedListener(OnTagsDetectedListener listener) {
        this.listener = listener;
    }

    // Repeating loop which creates tag events passed back to main()
    public void start() {
        CAENRFIDReader reader = new CAENRFIDReader();

        try {
            // IMPORTANT: CHANGE TO WHATEVER PORT IS BEING USED BY CURRENT DEVICE!
            reader.Connect(CAENRFIDPort.CAENRFID_RS232, "COM7");

            CAENRFIDLogicalSource source = reader.GetSource("Source_0");
            reader.SetPower(450);

            while (true) {
                CAENRFIDTag[] tags = source.InventoryTag();

                if(tags != null && tags.length > 0 && listener != null) {
                    listener.onTagsDetected(tags);
                } else {
                    System.out.println("no tags detected this cycle");
                }

                try {
                    Thread.sleep(500); // Wait 500ms before the next scan
                } catch (InterruptedException e) {
                    System.out.println("Reader thread interrupted.");
                }
            }

        } catch (CAENRFIDException ex) {
            Logger.getLogger(Superstarsrfid.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
