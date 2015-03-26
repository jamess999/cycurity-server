#!/usr/bin/python
import RPi.GPIO as GPIO
import time
import sys

pin = 16
freq = 50
ms = 20
right = (0.5/ms)*100
left = (2.5/ms)*100
neutral = (1.5/ms)*100
sleep = 2

if sys.argv[1] == "status":
        print "Not implemented"

else:
        GPIO.setmode(GPIO.BOARD)
        GPIO.setwarnings(False)
        GPIO.setup(pin,GPIO.OUT)
        p = GPIO.PWM(pin, freq)
        p.start(neutral)

        try:
                if sys.argv[1] == "left" or sys.argv[1] == "unlock":
                        p.ChangeDutyCycle(neutral)
                        print "Unlocking..."
                        time.sleep(sleep)
                elif sys.argv[1] == "right":
                        p.ChangeDutyCycle(right)
                        #time.sleep(sleep)
                        print "This function is disabled"
                elif sys.argv[1] == "neutral" or sys.argv[1] == "lock":
                        p.ChangeDutyCycle(left)
                        print "Locking..."
                        time.sleep(sleep)

        except KeyboardInterrupt:
                print "Failed."
                p.stop()
                GPIO.cleanup()
p.stop()
GPIO.cleanup()
