 #include <Arduino.h>
 #include "bluetoothController.h"
 #include "motorController.h"
 #include "accelerometerController.h"


double x_Multiplier = 36/1080; // 36 inches per 1080 pixels
double y_Multiplier = 39.6/1188; // 39.6 inches per 1188 pixels

int buttonPin = 8;


// the main file that runs the Arduino code

void setup() {

    // setup the pins for button
    pinMode(buttonPin, INPUT);


    // setup the bluetooth
    Serial.begin(9600);
    Serial1.begin(9600);  //Default Baud for comm, it may be different for your Module. 
    Serial.println("The bluetooth gates are open.\n Connect to HC-05 from any other bluetooth device with 1234 as pairing key!.");  

    String coordinateString = getCoordinates();
    Serial.println(coordinateString);
   
    // parse the input and create an array

    // get the number of points
    int pointCount = 0;
    int xIndex = 7;
    while (true) {
        if (coordinateString.substring(xIndex, xIndex+2) == "x:") {
            pointCount = (coordinateString.substring(7, xIndex)).toInt();
            break;
        } else {
            xIndex++;
        }
    }

    // create an array to store points
    double coordinatePoints [pointCount][3] = {};

    // read in the points
    int pointIndex = 0;
    int x = 0;
    int y = 0;
    int shots = 0;
    Serial.println(pointCount + "PointCOunt");
    while (pointIndex < pointCount) {
        if (coordinateString.substring(xIndex,xIndex+2) == "x:") {
            // find when y is
            int yIndex = xIndex+2;
            while (true) {
                if (coordinateString.substring(yIndex, yIndex+2) == "y:") {
                    x = (coordinateString.substring(2,yIndex)).toInt();
                    break;
                } else {
                    yIndex++;
                } 
            }
            // find when shots is
            int shotsIndex = yIndex+2;
            while (true) {
                if (coordinateString.substring(shotsIndex, shotsIndex+6) == "shots:") {
                    y = (coordinateString.substring(yIndex+2,shotsIndex)).toInt();
                    break;
                } else {
                    shotsIndex++;
                } 
            }

            // loop until either x or end
          
            if (pointIndex < pointCount - 1) {
                // still another point to read
                int nextIndex = shotsIndex+6;
                while (true) {
                    Serial.println("loop");
                    if (coordinateString.substring(nextIndex, nextIndex+2) == "x:") {
                        shots = (coordinateString.substring(shotsIndex+6,nextIndex)).toInt();
                        break;
                    } else {
                        nextIndex++;
                    } 
                }
                Serial.println("out");
                coordinateString = coordinateString.substring(nextIndex, coordinateString.length());
                Serial.println(coordinateString);
                xIndex = 0;
            } else {
                // no more points to read
                shots = (coordinateString.substring(shotsIndex+6, coordinateString.length())).toInt(); 
            }
           
 
            Serial.println(x);
            Serial.println(y);
            Serial.println(shots);


            coordinatePoints[pointIndex][0] = ((x * x_Multiplier) - (36/2));
            coordinatePoints[pointIndex][1] = ((39.6/2) - (y * y_Multiplier));
            coordinatePoints[pointIndex][2] = shots;

            pointIndex++;


        }
    }
    // finished getting all the points

    // setup array for hits
    int hitCount[pointCount] = {0};


    // set up the motor pins
    setupMotor();
    setupAccelerometer();

    // move to motor state and waiting for user to press button
    int positionIndex = 0;
    int buttonRead = 0;
    boolean hit = false;
    while (positionIndex < pointCount) {
       // Serial.println("Before ");
       // Serial.println(coordinatePoints[positionIndex][0]);
       // Serial.println(coordinatePoints[0][0]);
        //Serial.println(coordinatePoints[0][1]);
        //Serial.println(coordinatePoints[1][0]);
        //Serial.println(coordinatePoints[1][1]);
        driveTo(Point((coordinatePoints[positionIndex][0])/12, (coordinatePoints[positionIndex][1])/12));
        //digitalWrite(13,HIGH);

        while (true) {
             // check hits and wait for button to be pressed

            // check for hits
            hit = hitDetection();
            if (hit) {
                Serial.println("Hit");
                hitCount[positionIndex] +=1;
            }

            // delay 3 seconds after hit
            delay(3000);


            buttonRead = digitalRead(buttonPin);
            Serial.println(buttonRead);
            if (buttonRead == HIGH) {
                Serial.println("High");
                Serial.println(hitCount[positionIndex]);
                // move to next point or end
                positionIndex ++;
                break;
            }
        }
       // Serial.println("Postion" + positionIndex);
        Serial.flush();
    }

    // wait for button to be pressed to send code to app
}

void loop() {

}
