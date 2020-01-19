#include<Arduino.h>
#include <Wire.h>
#include <MMA7660.h>
#include "accelerometerController.h"

MMA7660 accelemeter;


//float ax,ay,az;
int8_t n = -5;


void setupAccelerometer()
{
   accelemeter.init();
   
}

bool hitDetection(){



  /*int8_t x, y, z;
  accelemeter.getXYZ(&x,&y,&z);
  if ((y <= (n-12)) || (y >= (n+12))){
    return true;
  } 
  else if ((x+8 <= (n-12)) || (x+8 >= (n+12))){
    return true;
  }
  else if ((z+12 <= (n-12)) || (z+12 >= (n+12))){
    return true;
  }
  else {
    return false;
  }
  delay(100);*/
  return false;
}



