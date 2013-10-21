#define FADE_PIN 9

int led = 13;
int sensorValue0 = 0;
int sensorValue1 = 0;
const int analogInPin0 = A0;
const int analogInPin1 = A1;
long timer = 0;
// How often to check - long intervals
const long longInterval = 86400000; 
// For testing proposes
//const long longInterval = 38000;
const long waterNow = 28000;
boolean isDry = false;
long elapsedTime = longInterval;
// Set the dry threshould 
const int dryValue = 700 ;
int counter = 0;
const int maxRuns = 3;

void setup() {
  Serial.begin(115200);
  pinMode(FADE_PIN, OUTPUT);  
  pinMode(led, OUTPUT);   
}
void activateForTenSeconds() {
      analogWrite(FADE_PIN, 255);
      digitalWrite(led, HIGH);
      delay(10000);
      analogWrite(FADE_PIN, 0);
      digitalWrite(led, LOW);
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
    //Serial.print("received: ");
    //Serial.println(incomingByte);
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
  }
  if ((timer + elapsedTime) < millis()){

    
    if (isDry) {
      elapsedTime = waterNow;
      activateForTenSeconds();
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
}
