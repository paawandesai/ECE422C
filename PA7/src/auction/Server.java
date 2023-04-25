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
	private List<AuctionItem> items = new ArrayList<>();
	private ClientHandler handler;

	public static void main(String[] args) {
		new Server().runServer();
	}

	private void runServer() {
		try {
			setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4274);
		while (true) {
			Socket clientSocket = serverSock.accept();
			System.out.println("Connecting to... " + clientSocket);
			handler = new ClientHandler(this, clientSocket);
			this.addObserver(handler);
			Thread t = new Thread(handler);
			t.start();
		}
	}

	protected void processRequest(String input, ClientHandler clientHandler) {
		Gson gson = new Gson();
		String[] parts = input.split(" ");
	    if (parts.length == 3 && parts[0].equals("bid")) {
	        int itemId = Integer.parseInt(parts[1]);
	        double bidAmount = Double.parseDouble(parts[2]);
	        boolean validBid = isValidBid(itemId, bidAmount);
	        if (validBid) {
	            // Notify all clients of new highest bid
	            setChanged();
	            notifyObservers(gson.toJson(new Message("newBid", "", itemId)));
	            // Update the corresponding AuctionItem with the new bid amount
	            for (AuctionItem item : items) {
	                if (item.getAuctionItemId() == itemId) {
	                    //item.setHighestBidder(clientHandler.getClientId());
	                    item.setHighestBid(String.valueOf(bidAmount));
	                    break;
	                }
	            }
	        }
	        // Send response to client
	        Message outputMessage = new Message("output", Boolean.toString(validBid), 0);
	        clientHandler.sendToClient(gson.toJson(outputMessage));
	    }
		else if (parts.length == 1 && parts[0].equals("items")) {
			String fileName = "items.txt";
			String fileContent = "";
			try {
				BufferedReader reader = new BufferedReader(new FileReader(fileName));
				String line;
				while ((line = reader.readLine()) != null) {
					fileContent += line;
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Message outputMessage = new Message("output", fileContent, 0);
			clientHandler.sendToClient(gson.toJson(outputMessage));
		} 
		else {
			String output = "Error: ";
			clientHandler.sendToClient(gson.toJson(new Message("output", output, 0)));
		}
	}


	private void sendItemList(ClientHandler clientHandler) {
		List<AuctionItem> itemList = readItemsFromFile();
		Gson gson = new Gson();
		String itemListJson = gson.toJson(itemList);
		Message message = new Message("items", itemListJson, 0);
		clientHandler.sendToClient(gson.toJson(message));
	}

	List<AuctionItem> readItemsFromFile() {
		List<AuctionItem> itemList = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("items.txt"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 4) {
					Integer itemId = Integer.parseInt(parts[0]);
					String name = parts[1];
					String description = parts[2];
					String startPrice = parts[3];
					itemList.add(new AuctionItem(itemId, name, description, startPrice));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return itemList;
	}
	private synchronized boolean isValidBid(int itemId, double bidAmount) {
		for (AuctionItem item : items) {
			if (item.getAuctionItemId() == itemId) {
				// Check if auction is still open
				if (item.closed()) {
					return false;
				}
				return item.bid(item.getHighestBidder(), bidAmount);
			}
		}
		// Item not found
		return false;
	}


}
