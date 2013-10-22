#define FADE_PIN 9

int led = 13;
int sensorValue0 = 0;
int sensorValue1 = 0;
const int analogInPin0 = A0;
const int analogInPin1 = A1;
long timer = 0;
// How often to check - long intervals
// Once a day
//const long longInterval = 86400000;
// Twice a day
const long longInterval = 43200000;
// For testing proposes
//const long longInterval = 38000;
const long waterNow = 38000;
boolean isDry = false;
long elapsedTime = longInterval;
//long elapsedTime = 0;
// Set the dry threshould 
const int dryValue = 700;
int counter = 0;
const int maxRuns = 3;

long pumpTimer = 0;
boolean isPumpActive = false;
long elapsedPumpTime = 20000;
      
      
void setup() {
  Serial.begin(115200);
  pinMode(FADE_PIN, OUTPUT);  
  pinMode(led, OUTPUT);   
}
void touglePump(){
  isPumpActive = !isPumpActive;
  if (isPumpActive) {
      analogWrite(FADE_PIN, 255);
      digitalWrite(led, HIGH);
  } else {
      analogWrite(FADE_PIN, 0);
      digitalWrite(led, LOW);
  }
}
void loop() {
  sensorValue0 = analogRead(analogInPin0);
  sensorValue1 = analogRead(analogInPin1);
  
  if (((sensorValue0 + sensorValue1)/2) < dryValue) {
    isDry = true;
  } else {
    isDry = false;
  }
  if (Serial.available() > 0) {
    // read the incoming byte:
    int incomingByte = Serial.read();
    
    if(incomingByte == 104){
      Serial.print("Dry: ");
      Serial.println(dryValue);
    }
    if(incomingByte == 105){
      Serial.print("Average: ");
      Serial.println(((sensorValue0 + sensorValue1)/2));
    }
    if(incomingByte == 106){
      Serial.print("Sensor0: ");
      Serial.println(sensorValue0);
    }
    if(incomingByte == 107){
      Serial.print("Sensor1: ");
      Serial.println(sensorValue1);
    }
    if(incomingByte == 108){
      Serial.print("Elapsed: ");
      Serial.println((elapsedTime + timer) - millis());
    }
  }
  if ((timer + elapsedTime) < millis()){

    
    if (isDry) {
      elapsedTime = waterNow;
      pumpTimer = millis();
      // Activate the pump
      if (!isPumpActive){
        touglePump();
      }
      ++counter;
    } else {
      elapsedTime = longInterval; 
      counter = 0;
    }
    if (counter > maxRuns) {
      counter = 0;
      elapsedTime = longInterval; 
    }
    timer = millis();
  }
  if (isPumpActive){
     if ((pumpTimer + elapsedPumpTime) < millis()){
       // Turn pump off
       touglePump(); 
     }
  }
}
