/*
 * EE422C Project 2 (Mastermind) submission by
 * Replace <...> with your actual data. 
 * <Paawan Desai>
 * <pkd397>
 * Slip days used: <0>
 * Spring 2023
 */
package assignment2;

import java.util.Scanner;

public class Driver {
    public static void main(String[] args) {
    	String[] colors = {"B","G","O","P","R","Y"};
    	GameConfiguration config = new GameConfiguration(12, colors, 4);
    	SecretCodeGenerator secretCodeGenerator = new SecretCodeGenerator(config);
    	start(false, config, secretCodeGenerator);
	}

    public static void start(Boolean isTesting, GameConfiguration config, SecretCodeGenerator generator) {
        // TODO: complete this method
		// We will call this method from our JUnit test cases.
		System.out.println("Welcome to Mastermind.");
		System.out.println("Do you want to play a new game? (Y/N):");
		Scanner input = new Scanner(System.in);
		String toPlay = input.nextLine();
		while(toPlay.equals("Y")) {
			GuessList guessList = new GuessList();
			Game newGame = new Game(generator, config, isTesting, input, guessList, config.guessNumber);
			toPlay = newGame.runGame();
		}
	}
		
		
}