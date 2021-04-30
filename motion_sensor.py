
# Import required Python libraries

import RPi.GPIO as GPIO
import time
import subprocess
from pyrebase import pyrebase
GPIO.setwarnings(False)

#database configuration
config = {
    "apiKey":"AlzaSyDC6lpDBbhiqcPEzeTB-0DVmqg-WBpTZDw",
    "authDomain":"smartspace-ff9cf.firebaseapp.com",
    "databaseURL":"https://smartspace-ff9cf.firebaseio.com/",
    "storageBucket":"smartspace-ff9cf.appspot.com"
    }
    
firebase = pyrebase.initialize_app(config)
auth = firebase.auth()

database = firebase.database()
ref = database


GPIO.setmode(GPIO.BOARD)
PIR_PIN = 31
GPIO.setup(PIR_PIN, GPIO.IN)
GPIO.setup(16, GPIO.OUT)
GPIO.setup(18, GPIO.OUT)

current_state=0
previous_state=0
counter=0

GPIO.output(16, GPIO.HIGH)
GPIO.output(18, GPIO.LOW)
try:
    print('waiting for sensor to settle')
    
    #loop until pir is 0
    while GPIO.input(PIR_PIN)==1:
        current_state=0
        
        GPIO.output(16, GPIO.HIGH)
        GPIO.output(18, GPIO.LOW)
    print('ready')
    
    while True:
        
        #read pir state
        current_state=GPIO.input(PIR_PIN)
       
        
        if current_state==1 and previous_state==0:
            #pir is triggered
            print('motion detected')
            if counter==0:
                print('space now occupied')
                ref.child("carparks").child("pnvsluFR0ae0UkEiRRpP1qk9Cce2").child("Layout").child("0").child("spaces").child("3").child("active").set(True)
                GPIO.output(16, GPIO.LOW)
                GPIO.output(18, GPIO.HIGH)
                counter = counter+1
                time.sleep(2)
            elif counter==1:
                print('space is now free')
                ref.child("carparks").child("pnvsluFR0ae0UkEiRRpP1qk9Cce2").child("Layout").child("0").child("spaces").child("3").child("active").set(False)
                GPIO.output(16, GPIO.HIGH)
                GPIO.output(18, GPIO.LOW)
                counter= counter-1
                time.sleep(2)
            
            previous_state=1
        elif current_state==0 and previous_state==1:
            print('waiting to detect motion')
            previous_state=0
        
        time.sleep(2)
        
except KeyboardInterrupt:
    print('quit')
    GPIO.cleanup()
        

#while True:
 #   i = GPIO.input(PIR_PIN)
 #   if i==0:
  #      print('no motion')
  #      time.sleep(2)
  #  elif i==1:
  #      print('motion detected')
   #     time.sleep(2)
     #ref.child("raspberryPi").child("detected").set("occupied")
     
      #ref.child("raspberryPi").child("detected").set("free")