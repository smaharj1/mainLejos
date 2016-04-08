package com.lejos.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.util.ArrayList;

import com.lejos.controller.Robot;
import com.lejos.view.MainView;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class Algorithm {
	public static ArrayList<RuleNode> rules = new ArrayList<RuleNode>();
	private ArrayList<Pixel> boxes;
	
	private ArrayList<RuleNode> possibleRules = new ArrayList<RuleNode>();
	private ArrayList<RuleNode> rejectedRules = new ArrayList<RuleNode>();
	
	private ArrayList<RuleNode> previousState = new ArrayList<RuleNode>();
	
	private static ArrayList<String> rhsRules = new ArrayList<String>();
	
	//private Robot robot;
	
	FileAccess fileAccess;
	
	public Algorithm(String filename, Robot robot) {
//		boxes.add(new Pixel(0,0,true));
//		boxes.add(new Pixel(0,1,true));
//		boxes.add(new Pixel(1,0,false));
//		boxes.add(new Pixel(2,0,true));
//		boxes.add(new Pixel(2,1,true));
//		boxes.add(new Pixel(0,2,false));
//		//boxes.add(new Pixel(0,2,true));
		
		fileAccess = new FileAccess(filename);
			
		//this.robot = robot;
		//robot = new Robot();
	}
	
	public static void addRule(String str) {
		RuleNode temp = new RuleNode(str);
		rules.add(temp);
		
		rhsRules.add(temp.getRHS());
	}
	
	public RuleNode find(String str) {
		for (int i =0; i < rules.size(); ++i) {
			RuleNode currentRule = rules.get(i);
			if (currentRule.getRHS().equals(str)) {
				return currentRule;
			}
		}
		
		return null;
	}
	
	public void forward(Robot r) {
		initiateForwardCondition();
		
		Robot robot = r;
		boxes = new ArrayList<Pixel>();
		
		for (int row = 0; row <6; ++row ) {
			for (int col = 0; col < 8; ++col) {
				robot.travelOne();
				
				if (robot.isON()) {
					robot.soundON();
					boxes.add(new Pixel(row,col,true));
					updatePossibilities(new Pixel(row, col, true));
				}
				else {
					robot.soundOFF();
					updatePossibilities(new Pixel (row,col, false));
				}
				
				LCD.drawString("Pixel: "+ row+" "+col, 0, 0);
				
				for (int index = 0; index < possibleRules.size(); ++ index) {
					LCD.drawString(possibleRules.get(index).getRHS()+"", index * 2, 1);
				}
								
				if (!possibleRules.isEmpty()) {
					previousState = possibleRules;
				}
				else {
					robot.soundFailure();
					possibleRules = previousState;
				}
				
				Delay.msDelay(1000);
				LCD.clear();
			}
			LCD.drawString("Reconfigure if you have to", 0, 0);
			Delay.msDelay(3000);
			LCD.clear();
		}	
		
		ArrayList<String> substrings = new ArrayList<String>();
		
		substrings = getSubStrings();
		
		
		// Prints the result at the last
		for (int index = 0; index < possibleRules.size(); ++ index) {
			LCD.drawString(possibleRules.get(index).getRHS()+"", index * 2, 1);
		}
		
		// Prints all of the substrings
		for (int subIndex=0; subIndex < substrings.size(); subIndex++) {
			LCD.drawString(substrings.get(subIndex)+"", subIndex * 2, 3);
		}
		
		Delay.msDelay(4000);
	}
	

	public ArrayList<String> getSubStrings() {
		
		ArrayList<String> substrings = new ArrayList<String>();
		
		ArrayList<Pixel> lhsRules = new ArrayList<Pixel>();
		
		for (int mainIndex = 0; mainIndex < rules.size(); ++mainIndex) {
			lhsRules = rules.get(mainIndex).getLHSPixels();
			substrings.add(rules.get(mainIndex).getRHS());
			
			// Here we go through each and every pixels of the rule. This should be changed for different platforms
			for (int i = 0; i < boxes.size(); ++i) {	
				Pixel testCoord = boxes.get(i);
						
				for (int index = 0; index < lhsRules.size(); index++) {
					Pixel ruleCoord = lhsRules.get(index);
					
					// At this point, check if the robot generated coordinates is equal to the ones in the list and if it is on. If not, then error
					if (testCoord.getRow() == ruleCoord.getRow() && testCoord.getColumn() == ruleCoord.getColumn()) {
						if (!testCoord.isOn()) {
							//LCD.drawString("Sorry not found",0,0);
							substrings.remove(substrings.size()-1);
						}
					}
				}
			}
	
			// At this point, character is recognized
			
		}
		
		return substrings;
	}
	private void updatePossibilities(Pixel input) {
		if (!input.isOn()) {
			// Now, check which rules have the pixel and eliminate it
			// Because the input from Robot don't have that pixel light up
			for (int result = 0; result < possibleRules.size(); ++result) {
				if (RuleNode.checkIfExists(input, possibleRules.get(result))) {
					rejectedRules.add(possibleRules.get(result));
					possibleRules.remove(result);
					result--;
				}
			}
		}
	}
	
	private void initiateForwardCondition() {
		possibleRules = rules;
		previousState = rules;
	}
	
	public void backward(String str, Robot robot) {
		Delay.msDelay(2000);
		RuleNode mainRule = find(str);
		
		// Checks if the main rule is null. If it is not null, then check if the LHS of the 
		// rule follows all the rules
		if (mainRule != null) {
			ArrayList<Pixel> lhsRules = new ArrayList<Pixel>();
			
			lhsRules = mainRule.getLHSPixels();
								
			// Here we go through each and every pixels of the rule. This should be changed for different platforms
			// ROBOT FUNCTIONS GO HERE
			
			for (int row = 0; row <6; ++row ) {
				for (int col = 0; col < 8; ++col) {
					robot.travelOne();
					
					Pixel testCoord;
					if (robot.isON()) {
						robot.soundON();
						//LCD.drawString("is on", 0, 0);
						testCoord = new Pixel(row, col, true);
					}
					else {
						robot.soundOFF();
						//LCD.drawString("is off and " + robot.getOFF(), 0, 0);
						testCoord = new Pixel(row, col, false);
					}		
					
					//Delay.msDelay(2000);
					for (int index = 0; index < lhsRules.size(); index++) {
						Pixel ruleCoord = lhsRules.get(index);
						
						// At this point, check if the robot generated coordinates is equal to the ones in the list and if it is on. If not, then error
						if (testCoord.getRow() == ruleCoord.getRow() && testCoord.getColumn() == ruleCoord.getColumn()) {
							if (!testCoord.isOn()) {
								robot.soundFailure();
								LCD.drawString("Sorry not found",0,0);
								return;
							}
						}
					}
					
					LCD.drawString(row+" "+col, 0, 1);
					
					Delay.msDelay(1000);
					LCD.clear();
				}
				LCD.drawString("Reconfigure if you have to", 0, 0);
				Delay.msDelay(3000);
				LCD.clear();
			}
			
			// At this point, character is recognized
			LCD.drawString("Character has been recognized",0,0);
			robot.soundSuccess();
		}
		else {
			LCD.drawString("It is null",0,0);
		}
		
		
	}
	
	/**
	 * Gets the rule coordinates of left hand side given the string on the right hand side
	 * @param value
	 * @return
	 */
	public static ArrayList<Pixel> getRules(String value) {
		if (rules.isEmpty()) {
			return null;
		}
		
		ArrayList<Pixel> lhsCoords = new ArrayList<Pixel>();
		
		for (int i =0; i < rules.size(); ++i){
			if (value.equals(rules.get(i).getRHS())) {
				return rules.get(i).getLHSPixels();
			}
		}
		
		return null;
	}
	
	public ArrayList<String> getRuleValues() {
		return rhsRules;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			    
		Algorithm algo = new Algorithm("text.txt", new Robot());
		
	    //Button.waitForAnyPress();
		
		//algo.backward("C");
	    
	}
		
		/*
		Charset charset = Charset.forName("US-ASCII");
		
		Path file = Paths.get("text.txt");
		
		
		
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
		    String line;
		    while ((line = reader.readLine()) != null) {
		        //System.out.println(line);
		        working.addRule(line);
		    }
		} catch (IOException x) {
		    LCD.drawString("IOException: %s%n", 0,0);
		}*/
	
//		Algorithm working = new Algorithm();
//		// Do backward search
//		String character = "D";
//		working.backward(character);
////		
//		
//		Delay.msDelay(3000);

	//}

}
