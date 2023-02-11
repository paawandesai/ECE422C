/*
 * EE422C Project 2 (Mastermind) submission by
 * Replace <...> with your actual data. 
 * <Paawan Desai>
 * <pkd397>
 * Slip days used: <0>
 * Spring 2023
 */
package assignment2;
import java.util.*;
public class GuessList {
	ArrayList<String> history;
	public GuessList(){
		history = new ArrayList<String>();
	}
	public void addGuess(String guess, String pegs) {
		String guesstopeg = guess + " -> " + pegs;
		history.add(guesstopeg);
	}
	public void printHistory() {
		for(int i = 0; i < history.size(); i++) {
			System.out.println(history.get(i));
		}
	}
}
