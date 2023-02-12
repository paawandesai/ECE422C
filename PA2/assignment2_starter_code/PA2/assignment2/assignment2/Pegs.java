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

public class Pegs {
	int blackPegs;
	int whitePegs;
	boolean usedGuess;
	String[] userCode;
	String[] secretCode;
	String pegs; 
	public Pegs(Guess newGuess, String secretCode) {
		userCode = newGuess.guess.split("");
		this.secretCode = secretCode.split("");
		compareToOrig();
		pegs = blackPegs + "b_" + whitePegs + "w";
	}
	public void compareToOrig() {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		ArrayList<Integer> whiteIndexes = new ArrayList<Integer>();
		for(int i = 0; i < userCode.length; i++ ) {
			if(userCode[i].equals(secretCode[i])) { 
				blackPegs++;
				indexes.add(i);
			}
		}
		for(int i = 0; i < userCode.length; i++) {
			usedGuess = false;
			if(indexes.contains(i) == false) {
				for(int j = 0; j < userCode.length; j++) {
					if(userCode[i].equals(secretCode[j])) {
						if(indexes.contains(j) == false && usedGuess == false && whiteIndexes.contains(j) == false) {
							usedGuess = true;
							whitePegs++;
							whiteIndexes.add(j);
						}
						
					}
				}
			}
			
		}
	}
}

