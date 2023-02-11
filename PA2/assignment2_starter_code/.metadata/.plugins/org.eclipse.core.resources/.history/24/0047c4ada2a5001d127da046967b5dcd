/* If you submit this file, it will be ignored. Do not modify. */
package assignment2;

import java.util.Random;

public class SecretCodeGenerator {

    private Random randomGenerator;
    private GameConfiguration config;

    SecretCodeGenerator(GameConfiguration config) {
        randomGenerator = new Random();
        this.config = config;
    }

    // Use this method for each game only once.
    public String getNewSecretCode() {
        StringBuilder result = new StringBuilder();
        int index, numberOfPegs = config.pegNumber;
        String[] colors = config.colors;
        for (int i = 0; i < numberOfPegs; i++) {
            index = randomGenerator.nextInt(colors.length);
            result.append(colors[index]);
        }
        return result.toString();
    }
}
