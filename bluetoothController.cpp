 #include <Arduino.h>
 #include "bluetoothController.h"



void send_line(char* string) {
    Serial1.write(string);
    Serial1.flush();
    Serial.write(string);
} 


String getCoordinates() {

    String coordinateString = "";
    String message = "";


    while (true) {
        // keep checkin for messages until the end is reached then return the coordinates
        if (Serial1.available() > 0) {
            char in_char = Serial1.read();
           // Serial.println(in_char);
            if (in_char == '\n' || in_char == '\r') {
                // Serial.println(message);
                if (message == "connected?" || message == "connected?\n") {
                    send_line("connected\nconnected\n");
                } else if (message.substring(0,7) == "points:") {
                    // create a new array of points
                   // int pointCount = (message.substring(7, message.length())).toInt();
                    coordinateString += message;
                } else if (message.substring(0,2) == "x:") {
                   coordinateString += message;
                } else if (message == "end:" || message == "end:\n") {
                    return coordinateString;
                }
                message = "";
            } else {
                message += in_char; 
            }       
        }
    }
    return coordinateString;
}

