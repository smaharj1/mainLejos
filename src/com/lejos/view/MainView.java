package com.lejos.view;

import com.lejos.model.Algorithm;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;

public class MainView {

	private GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
	private boolean isForward = true;
	
	private final int CURSOR_X = 10;
	final static String CURSOR = "<";
	
	public MainView() {
		
	}
		
	public void initiateMenu() {
		LCD.drawString(CURSOR, CURSOR_X, 0);
		
		while (true) {
			LCD.drawString("Forward", 0, 0);
			LCD.drawString("Backward", 0, 1);
			
			int buttonPressed = Button.waitForAnyPress();
			
			if (buttonPressed == Button.ID_DOWN && isForward) {
				clearCursor();
				LCD.drawString(CURSOR, CURSOR_X, 1);
				isForward = !isForward;
			}
			else if (buttonPressed == Button.ID_UP && !isForward) {
				clearCursor();
				LCD.drawString(CURSOR, CURSOR_X, 0);
				isForward = !isForward;
			}
			else if (buttonPressed == Button.ID_ENTER) {
				StartGameView startGame = new StartGameView(isForward);
			}
			else if (buttonPressed == Button.ID_ESCAPE) {
				break;
			}
		}
		
		
		
		
	}
	
	public void clearCursor() {
		LCD.drawString(" ", CURSOR_X, 0);
		LCD.drawString(" ", CURSOR_X, 1);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("Welcome to riddle");
		//System.out.println("Please select 1:");
		MainView view = new MainView();
		view.initiateMenu();
	}
	
	
}