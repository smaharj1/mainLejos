package com.lejos.controller;

import com.lejos.model.Pixel;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.utility.Delay;

public class Robot {
	private float travelDistance = 0;
	private int row = 0;
	private int col = 0;
	private int speed = 4;
	
	private final int RIGHT = 90;
	private final int LEFT = -90;
	
	private Pixel currentPixel;
	
	private static Port colorSensorPort = SensorPort.S4;
    private static EV3ColorSensor colorSensor=null;
    private static SampleProvider sampleProvider;
    private static int sampleSize;
    
    private float on = 0;
    private float off = 0;
    
    DifferentialPilot pilot;
	
    private static float getSample() {
        // Initializes the array for holding samples
        float[] sample = new float[sampleSize];

        // Gets the sample an returns it
        sampleProvider.fetchSample(sample, 0);
        return sample[0];
    }
	
	public Robot() {
		pilot = new DifferentialPilot(5.42f, 9.6f, Motor.A, Motor.C, false);
		travelDistance = (float) 3.0;
		pilot.setTravelSpeed(speed);
	}
	
	public void travelOne() {
		// Robot checks with count and see if it should go right, left or forward
		if (row ==7 && col == 5) {
			return;
		}
		else if (row %2 == 0) {
			if (col <= 5) {
				col ++;
				// Robot goes forward
				pilot.travel(travelDistance);
			}
			else {
				row ++;
				// Robot goes right
				pilot.travel(travelDistance);
				pilot.rotate(RIGHT);
				pilot.travel(travelDistance);
				pilot.rotate(RIGHT);
			}
		}
		else if (row % 2 == 1) {
			if (col >= 0) {
				col--;
				// Robot goes forward
				pilot.travel(travelDistance);
			}
			else {
				row++;
				// Robot goes left
				pilot.travel(travelDistance);
				pilot.rotate(LEFT);
				pilot.travel(travelDistance);
				pilot.rotate(LEFT);
			}
		}
	}
	
	public void recordON() {
		// Sense the color
		sampleProvider = colorSensor.getRedMode();
        sampleSize = sampleProvider.sampleSize();
		
		// record it in on
        on = getSample();
        String val = on+"value";
        LCD.drawString(val,0,2);
        Delay.msDelay(3000);
	}
	
	public void recordOFF() {
		// Sense the color
		sampleProvider = colorSensor.getRedMode();
        sampleSize = sampleProvider.sampleSize();
		
		// record it in on
        off = getSample();
        String val = off+"value";
        
        LCD.drawString(val,0,2);
        Delay.msDelay(3000);
	}
	
	public void startSensor() {
		if (colorSensor ==null) {
			colorSensor = new EV3ColorSensor(colorSensorPort);
		}
	}
	
	public float getON() {
		return on;
	}
	
	public float getOFF() {
		return off;
	}
	
	public boolean isON() {
		sampleProvider = colorSensor.getRedMode();
        sampleSize = sampleProvider.sampleSize();
        
        float value = getSample();
        LCD.drawString(value+" is read", 0, 0);
        Delay.msDelay(2000);
        float lowerBound = (float) (on * 0.75);
        float upperBound = (float) (on * 1.25);       
        
		if ( value < upperBound) {
			return true;
		}
        
		return false;
	}
	
	private float readColor() {
		sampleProvider = colorSensor.getRedMode();
        sampleSize = sampleProvider.sampleSize();
        
        return getSample();
	}
	
	public void readPixelSize(){
		LCD.clear();
		LCD.drawString("Place the sensor", 0, 1);
		LCD.drawString("within 5 seconds", 0, 2);
		Delay.msDelay(5000);
		LCD.clear();
		LCD.drawString("Scanning the pixelSize", 0, 1);

		pilot.forward();
		double time1 =0.00;
		double time2 = 0.00;
		boolean onColorRead = false;
		while(true){
			float colorRead = readColor();
			System.out.println();
			System.out.println();
			if(!onColorRead && (colorRead<=(on*1.2))){
				onColorRead = true;
				time1 = System.currentTimeMillis()/1000.00;
			}			
			else if(onColorRead && (colorRead>(on*1.2) )){
				time2 = System.currentTimeMillis()/1000.00;
				break;
			}
		}
		pilot.stop();
		LCD.clear();
		float pixelSize = (float) (speed*(time2-time1));	
		travelDistance = pixelSize;
		LCD.drawString("Pixel Size read as:", 0, 3);
		LCD.drawString(""+pixelSize, 0, 4);
		Delay.msDelay(5000);
		
	}
}