package auction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import com.google.gson.Gson;

class Server extends Observable {
	List<AuctionItem> items = new ArrayList<>();
	ClientHandler handler;
	
  public static void main(String[] args) {
    new Server().runServer();
  }

  private void runServer() {
	  try {
		  readItemsFromFile();
          setUpNetworking();
	  } catch (Exception e) {
		  e.printStackTrace();
		  return;
      }
  }
  private void setUpNetworking() throws Exception {
      @SuppressWarnings("resource")
      ServerSocket serverSock = new ServerSocket(4249);
      while (true) {
	      Socket clientSocket = serverSock.accept();
	      System.out.println("Connecting to... " + clientSocket);
	
	      handler = new ClientHandler(this, clientSocket);
	      this.addObserver(handler);
	
	      Thread t = new Thread(handler);
	      t.start();
      }
  }

  protected void processRequest(String input) {
	  String output = "Error";
	    Gson gson = new Gson();
	    Message message = gson.fromJson(input, Message.class);
	    try {
	        switch (message.type) {
	            case "upper":
	                output = message.input.toUpperCase();
	                break;
	            case "lower":
	                output = message.input.toLowerCase();
	                break;
	            case "strip":
	                output = message.input.replace(" ", "");
	                break;
	            case "itemList":
	                sendItemList(handler);
	                return; // exit method without notifying observers
	        }
	        this.setChanged();
	        this.notifyObservers(output);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
   private void sendItemList(ClientHandler clientHandler) {
	  List<AuctionItem> itemList = readItemsFromFile();
	  Gson gson = new Gson();
	  String itemListJson = gson.toJson(itemList);
	  Message message = new Message("items", itemListJson, 0);
	  clientHandler.sendToClient(gson.toJson(message));
   }

	private List<AuctionItem> readItemsFromFile() {
	  List<AuctionItem> itemList = new ArrayList<>();
	  try (BufferedReader br = new BufferedReader(new FileReader("items.txt"))) {
	    String line;
	    while ((line = br.readLine()) != null) {
	      String[] parts = line.split(",");
	      if (parts.length >= 4) {
	        Integer itemId = Integer.parseInt(parts[0]);
	        String name = parts[1];
	        String description = parts[2];
	        double startPrice = Double.parseDouble(parts[3]);
	        itemList.add(new AuctionItem(itemId, name, description, startPrice));
	      }
	    }
	  } catch (IOException e) {
	    e.printStackTrace();
	  }
	  return itemList;
	}


}
