# Bluetooth-terminal-simple

This is a simple project for turning the lights on and off.You can modify it to your own content.


# The Schematic is given below:
![alt text](https://hackster.imgix.net/uploads/attachments/273065/relay_bb_jHQ48yEvCR.png?auto=compress%2Cformat&w=680&h=510&fit=max)



# Requirements:
  1.arduino 
  2.bluetoothmodule HC-05
  3.relay(real life implementation)
  4.Two resistors (1k and 2.2k) for voltage dividing
  5.led
  6.breadboard and wire
  
  
# The thing u should know:
The bluetooth module uses 3.6-6V as Power but the RX pin can handle only 3.3V.
The arduino pushes 5V logic to bluetooth module.So we have to handle the voltage by droping it to 3.3 using the "voltage divider"(Just google it)
Now set up the circuit.Some high res pic are also given

![alt text](https://github.com/NaheedRayan/Readme-Images/blob/master/Bluetooth%20project%20simple/IMG_20200812_140750.jpg?raw=true)
<img src="github.com/NaheedRayan/Readme-Images/blob/master/Bluetooth%20project%20simple/IMG_20200812_140851.jpg?raw=true" width="300" height="400" title="Github Logo">
<img src="https://github.com/NaheedRayan/Readme-Images/blob/master/Bluetooth%20project%20simple/IMG_20200812_140902.jpg?raw=true" width="300" height="400" title="Github Logo">
<img src="https://github.com/NaheedRayan/Readme-Images/blob/master/Bluetooth%20project%20simple/IMG_20200812_140914.jpg?raw=true" width="300" height="400" title="Github Logo">


# Upload the code to arduino
#include <SoftwareSerial.h> 
#define RELAY 10 
#define LIGHT 13 
SoftwareSerial btm(2,3); // rx tx 
int index = 0; 
char data[10]; 
char c; 
boolean flag = false;
void setup() { 
 pinMode(RELAY,OUTPUT); 
 pinMode(LIGHT,OUTPUT); 
 digitalWrite(RELAY,HIGH); 
 digitalWrite(LIGHT,LOW); 
 btm.begin(9600); 
} 
void loop() { 
   if(btm.available() > 0){ 
     while(btm.available() > 0){ 
          c = btm.read(); 
          delay(10); //Delay required 
          data[index] = c; 
          index++; 
     } 
     data[index] = '\0'; 
     flag = true;   
   }  
   if(flag){ 
     processCommand(); 
     flag = false; 
     index = 0; 
     data[0] = '\0'; 
   } 
} 
void processCommand(){ 
 char command = data[0]; 
 char inst = data[1]; 
 switch(command){ 
   case 'R': 
         if(inst == 'Y'){ 
           digitalWrite(RELAY,LOW); 
           btm.println("Relay: ON"); 
         } 
         else if(inst == 'N'){ 
           digitalWrite(RELAY,HIGH); 
           btm.println("Relay: OFF"); 
         } 
   break; 
   case 'L': 
         if(inst == 'Y'){ 
           digitalWrite(LIGHT,HIGH); 
           btm.println("Light: ON"); 
         } 
         else if(inst == 'N'){ 
           digitalWrite(LIGHT,LOW); 
           btm.println("Light: OFF"); 
         } 
   break; 
 } 
} 


# Pro tip: if u are using Serial for rx and tx then pull the wires connecting it .Then upload the code.After uploading connect the wires😎😎

# Android app:The source code is given.
  What to modify? Modify the mac address in the MainActivity with you bluetooth module.
  While pairing with bluetooth module give access to contacts and call history.This is important.🙄🙄🙄 

![alt text](https://github.com/NaheedRayan/Readme-Images/blob/master/Bluetooth%20project%20simple/Screenshot_2020-08-12-14-11-34-168_com.android.settings.jpg?raw=true|width=100)
![alt text](https://github.com/NaheedRayan/Readme-Images/blob/master/Bluetooth%20project%20simple/Screenshot_2020-08-12-14-11-48-953_com.android.settings.jpg?raw=true)

# For more info u can checkout this link
# https://create.arduino.cc/projecthub/azoreanduino/simple-bluetooth-lamp-controller-using-android-and-arduino-aa2253

# Some apps in appstore u can download:
# https://play.google.com/store/apps/details?id=de.kai_morich.serial_bluetooth_terminal&hl=en






