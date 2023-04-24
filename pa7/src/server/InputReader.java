package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.StringTokenizer;

public class InputReader {
    private BufferedReader reader;
    private StringTokenizer tokenizer;

    public InputReader() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        tokenizer = null;
    }

    public String readLine() {
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public String readString() {
        while (tokenizer == null || !tokenizer.hasMoreTokens()) {
            tokenizer = new StringTokenizer(readLine());
        }
        return tokenizer.nextToken();
    }

    public int readInt() {
        try {
            return Integer.parseInt(readString());
        } catch (NumberFormatException e) {
            throw new InputMismatchException();
        }
    }

    public double readDouble() {
        try {
            return Double.parseDouble(readString());
        } catch (NumberFormatException e) {
            throw new InputMismatchException();
        }
    }

    public long readLong() {
        try {
            return Long.parseLong(readString());
        } catch (NumberFormatException e) {
            throw new InputMismatchException();
        }
    }
    
    public boolean readBoolean() {
        String s = readString();
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("y");
    }

    public char readChar() {
        return readLine().charAt(0);
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
