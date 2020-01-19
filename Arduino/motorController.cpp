#include <Arduino.h>
#include "Stepper.h"
#include "motorController.h"
#include <math.h>

#define CW true
#define CCW false
#define FORWARD false
#define BACKWARD true

// This is a complete complete guess
// CHANGE THIS, TODO
#define STEPS_PER_360 2535
unsigned long STEPS_PER_FOOT = 6621;



PinSet leftMotor(9, 10, 11, 12);
PinSet rightMotor(3, 4, 6, 7);



Point position(0, 0);
double theta = PI / 2;

void OneStep(bool dir, int pin1, int pin2, int pin3, int pin4, int step_number) {
    if (dir) {
        switch(step_number) {
        case 0:
            digitalWrite(pin1, HIGH);
            digitalWrite(pin2, LOW);
            digitalWrite(pin3, LOW);
            digitalWrite(pin4, LOW);
            break;
        case 1:
            digitalWrite(pin1, LOW);
            digitalWrite(pin2, HIGH);
            digitalWrite(pin3, LOW);
            digitalWrite(pin4, LOW);
            break;
        case 2:
            digitalWrite(pin1, LOW);
            digitalWrite(pin2, LOW);
            digitalWrite(pin3, HIGH);
            digitalWrite(pin4, LOW);
            break;
        case 3:
            digitalWrite(pin1, LOW);
            digitalWrite(pin2, LOW);
            digitalWrite(pin3, LOW);
            digitalWrite(pin4, HIGH);
            break;
        } 
    } else {
        switch(step_number) {
        case 0:
            digitalWrite(pin1, LOW);
            digitalWrite(pin2, LOW);
            digitalWrite(pin3, LOW);
            digitalWrite(pin4, HIGH);
            break;
        case 1:
            digitalWrite(pin1, LOW);
            digitalWrite(pin2, LOW);
            digitalWrite(pin3, HIGH);
            digitalWrite(pin4, LOW);
            break;
        case 2:
            digitalWrite(pin1, LOW);
            digitalWrite(pin2, HIGH);
            digitalWrite(pin3, LOW);
            digitalWrite(pin4, LOW);
            break;
        case 3:
            digitalWrite(pin1, HIGH);
            digitalWrite(pin2, LOW);
            digitalWrite(pin3, LOW);
            digitalWrite(pin4, LOW);
        } 
    }
}

double toAbsoluteAngle(double y, double x) {
    double theta = atan(y / x);
    if (x >= 0 && y >= 0) {
        return theta;                   // theta > 0
    } else if (x <= 0 && y >= 0) {
        return PI + theta;             // theta < 0
    } else if (x <= 0 && y <= 0) {
        return PI + theta;             // theta > 0
    } else {
        return 2 * PI + theta;             // theta < 0
    }
}

void turnTowards(Point p2) {

    Point rel_p2(p2.x - position.x, p2.y - position.y);
    double phi = toAbsoluteAngle(rel_p2.y, rel_p2.x);

    double dTheta = phi - theta;

    bool dir = dTheta > 0 ? CCW : CW;
    dTheta = abs(dTheta);

    if (dTheta > PI) {
        dTheta = 2 * PI - abs(dTheta);
        dir = !dir;
    }

    // Now we have the direction, and the amount of angle we need to rotate

    unsigned long numSteps = STEPS_PER_360 * abs(dTheta);

    // Now we know the number of steps for each wheel

    for (unsigned long step = 0; step < numSteps; step++) {
        if (dir == CCW) {
            OneStep(FORWARD, leftMotor.pin1, leftMotor.pin2, leftMotor.pin3, leftMotor.pin4, step % 4);
            OneStep(FORWARD, rightMotor.pin1, rightMotor.pin2, rightMotor.pin3, rightMotor.pin4, step % 4);
        } else {
            OneStep(BACKWARD, leftMotor.pin1, leftMotor.pin2, leftMotor.pin3, leftMotor.pin4, step % 4);
            OneStep(BACKWARD, rightMotor.pin1, rightMotor.pin2, rightMotor.pin3, rightMotor.pin4, step % 4);
        }
        delay(3);
    }

    theta = phi;
    if (theta > (2 * PI)) theta -= (2 * PI);

}

double distance(Point p1, Point p2) {
    return sqrt((p2.x - p1.x)*(p2.x - p1.x) + (p2.y - p1.y)*(p2.y - p1.y));
}

void driveTo(Point p2) {

    turnTowards(p2);

    double dr = distance(position, p2);
    Serial.println(dr);

    // Now drive forward

    unsigned long numSteps = STEPS_PER_FOOT * dr;

    // Now we know the number of steps

    for (unsigned long step = 0; step < numSteps; step++) {
        OneStep(BACKWARD, leftMotor.pin1, leftMotor.pin2, leftMotor.pin3, leftMotor.pin4, step % 4);
        OneStep(FORWARD, rightMotor.pin1, rightMotor.pin2, rightMotor.pin3, rightMotor.pin4, step % 4);
        delay(3);
    }

    position.x = p2.x;
    position.y = p2.y;

    turnTowards(Point(position.x, position.y + 1));

}

void setupMotor() {
    pinMode(leftMotor.pin1, OUTPUT);
    pinMode(leftMotor.pin2, OUTPUT);
    pinMode(leftMotor.pin3, OUTPUT);
    pinMode(leftMotor.pin4, OUTPUT);

    pinMode(rightMotor.pin1, OUTPUT);
    pinMode(rightMotor.pin2, OUTPUT);
    pinMode(rightMotor.pin3, OUTPUT);
    pinMode(rightMotor.pin4, OUTPUT);

   // delay(10000);

}

/*
void loop1() {

    driveTo(Point(position.x - 1, position.y - 1));
    // turnTowards(Point(position.x + 1, position.y - 1));

    // OneStep(FORWARD, leftMotor.pin1, leftMotor.pin2, leftMotor.pin3, leftMotor.pin4, step_number % 4);
    // OneStep(FORWARD, rightMotor.pin1, rightMotor.pin2, rightMotor.pin3, rightMotor.pin4, step_number % 4);
    delay(10000);

    // step_number++;
    // step_number = step_number % 4;
}*/
