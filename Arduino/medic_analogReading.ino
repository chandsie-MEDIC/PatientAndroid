void setup() {            
  
  Serial.begin(38400);
  delay(5000);
}

int i = 0;
void loop() {
  
  
  
  int sensorValue = analogRead(A0);
  Serial.println(sensorValue);
  delay(100);
}
