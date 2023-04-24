package auction;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class Client {

  private static String host = "127.0.0.1";
  private BufferedReader fromServer;
  private PrintWriter toServer;
  private Scanner consoleInput = new Scanner(System.in);

  public static void main(String[] args) {
    try {
      new Client().setUpNetworking();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setUpNetworking() throws Exception {
    @SuppressWarnings("resource")
    Socket socket = new Socket(host, 4249);
    System.out.println("Connecting to... " + socket);
    fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    toServer = new PrintWriter(socket.getOutputStream());

    Thread readerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        String input;
        try {
          while ((input = fromServer.readLine()) != null) {
            System.out.println("From server: " + input);
            processRequest(input);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    Thread writerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          String input = consoleInput.nextLine();
          String[] variables = input.split(",");
          Message request = new Message(variables[0], variables[1], Integer.valueOf(variables[2]));
          GsonBuilder builder = new GsonBuilder();
          Gson gson = builder.create();
          sendToServer(gson.toJson(request));
        }
      }
    });

    readerThread.start();
    writerThread.start();
  }

  protected void processRequest(String input) {
	    Gson gson = new Gson();
	    Message message = gson.fromJson(input, Message.class);

	    // check if the message type is "itemList"
	    if (message.type.equals("items")) {
	        // deserialize the JSON array of items
	        AuctionItem [] items = gson.fromJson(message.input, AuctionItem[].class);

	        // print out each item's details
	        for (AuctionItem item : items) {
	            System.out.println("Item ID: " + item.auctionItemId);
	            System.out.println("Name: " + item.name);
	            System.out.println("Description: " + item.description);
	            System.out.println("Start Price: " + item.startPrice);
	            System.out.println();
	        }
	    } else {
	        // process the input message as before
	        String output = "Error";
	        try {
	            String temp = "";
	            switch (message.type) {
	                case "upper":
	                    temp = message.input.toUpperCase();
	                    break;
	                case "lower":
	                    temp = message.input.toLowerCase();
	                    break;
	                case "strip":
	                    temp = message.input.replace(" ", "");
	                    break;
	            }
	            output = "";
	            for (int i = 0; i < message.number; i++) {
	                output += temp;
	                output += " ";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

  protected void sendToServer(String string) {
    System.out.println("Sending to server: " + string);
    toServer.println(string);
    toServer.flush();
  }

}
