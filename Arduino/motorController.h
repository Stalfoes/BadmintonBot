
#ifndef motorController_h
#define motorController_h



struct PinSet {
    PinSet(int pin1, int pin2, int pin3, int pin4)
    : pin1(pin1), pin2(pin2), pin3(pin3), pin4(pin4) { }
    int pin1, pin2, pin3, pin4;
};


struct Point {
    Point(double x, double y) : x(x), y(y) { }
    double x, y;
};



void OneStep(bool dir, int pin1, int pin2, int pin3, int pin4, int step_number);

double toAbsoluteAngle(double y, double x);

void turnTowards(Point p2);

double distance(Point p1, Point p2);

void driveTo(Point p2);

void setupMotor();





#endif
