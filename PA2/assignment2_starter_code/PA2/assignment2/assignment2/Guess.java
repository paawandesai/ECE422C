/*
 * EE422C Project 2 (Mastermind) submission by
 * Replace <...> with your actual data. 
 * <Paawan Desai>
 * <pkd397>
 * Slip days used: <0>
 * Spring 2023
 */
package assignment2;

public class Guess{
	
	int numberOfGuesses;
	String guess;
	GameConfiguration config;
	public Guess(GameConfiguration config, String guess) {
		this.config = config;
		this.guess = guess;
	}
	public int isValidGuess() {
		int codeLength = config.pegNumber;
		char guessChar;
		String guessString;
		if(guess.equals("HISTORY")) { 	
			return 0;
		}
		else if(guess.length() != codeLength ) { 
			return -1;
		}
		for(int i = 0; i < guess.length(); i++) { 
			guessChar = guess.charAt(i);
			guessString = Character.toString(guessChar);
			if(Character.isLowerCase(guessChar)) { 
				return -1;
			}
			guessString = Character.toString(guessChar);
			if(checkValidColor(guessString) == -1) { 
				return -1;
			}
		}
		return 1;
	}
	public int checkValidColor(String guessColor) {
		for(int i = 0; i < config.colors.length; i++) {
			if(guessColor.equals(config.colors[i])) {
				return 1;
			}
		}
		return -1;
	}
}
