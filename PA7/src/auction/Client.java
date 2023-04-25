package auction;

import java.util.Scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.application.Platform;
import javafx.stage.Stage;

class Client {

  private static String host = "127.0.0.1";
  private BufferedReader fromServer;
  private PrintWriter toServer;
  private Scanner consoleInput = new Scanner(System.in);
  ObjectOutputStream oos;

  public static void main(String[] args) {
    try {
      new Client().setUpNetworking();
	  javafx.application.Application.launch(LoginWindow.class);

      /*Platform.runLater(() -> {
    	    // code that interacts with JavaFX components goes here
    	  LoginWindow loginWindow = new LoginWindow();
          loginWindow.start(new Stage()); 
    	});  */
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Error connecting to server. Please check if the server is running.");
    }
  }

  private void setUpNetworking() throws Exception {
    @SuppressWarnings("resource")
    Socket socket = new Socket(host, 4274);
    System.out.println("Connecting to... " + socket);
    fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    toServer = new PrintWriter(socket.getOutputStream());
    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
    Thread readerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        String input;
        try {
          while ((input = fromServer.readLine()) != null) {
            System.out.println("From Server: " + input);
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
        	if (input.equals("items")) {
        	    Message request = new Message();
        	    request.type = "items";
        	    GsonBuilder builder = new GsonBuilder();
        	    Gson gson = builder.create();
        	    sendToServer(gson.toJson(request));
        	} else {
        	    Message request = new Message(input);
        	    GsonBuilder builder = new GsonBuilder();
        	    Gson gson = builder.create();
        	    sendToServer(gson.toJson(request));
        	}

        }
      }
    });
	
    readerThread.start();
    writerThread.start();
  }

  protected void processRequest(String input) {
	    Gson gson = new Gson();
	    Message message = gson.fromJson(input, Message.class);
	    String temp = "";
	    String output = "Error";
	    // check if the message type is "items"
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
	    } else if(message.type.equals("bid")){
	    	
	    }else if(message.type.equals("upper")) {
	    	temp = message.input.toUpperCase();
	    } else if (message.type.equals("lower")) {
	    	temp = message.input.toLowerCase();
	    } else if (message.type.equals("strip")) {
	    	temp = message.input.replace(" ", "");
	    }
	    output = "";
        for (int i = 0; i < message.number; i++) {
            output += temp;
            output += " ";
        }
	}

  protected void sendToServer(String string) {
    System.out.println("Sending to server: " + string);
    toServer.println(string);
    toServer.flush();
  }
  public void sendBid(String auctionItem, double bidAmount) {
	    try {
	        Message bidMessage = new Message(auctionItem, bidAmount);
	        oos.writeObject(bidMessage);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
  }
  public boolean updateBid(String itemId, String newBid) {
	    // Construct the message to update the bid for the given auction item
	    String message = "bid " + itemId + " " + newBid;

	    // Send the message to the server
	    sendToServer(message);
		return true;
	    
	}


}
