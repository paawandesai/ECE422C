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
import com.google.gson.reflect.TypeToken;

class Server extends Observable {
	private List<AuctionItem> auctionItems;
	private ClientHandler handler;
	private List<ClientHandler> clientHandlers;

	public Server() {
		auctionItems = readItemsFromFile();

	}


	public static void main(String[] args) {
		Server server = new Server();
		server.auctionItems = server.readItemsFromFile();
		print(server.auctionItems);
		server.runServer();
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
		ServerSocket serverSock = new ServerSocket(4276);
		while (true) {
			Socket clientSocket = serverSock.accept();
			System.out.println("Connecting to... " + clientSocket);
			handler = new ClientHandler(this, clientSocket);
			addClient(handler);

			this.addObserver(handler);
			Thread t = new Thread(handler);
			t.start();
		}
	}

	protected void processRequest(String input, ClientHandler clientHandler) {
		System.out.println("[server] got input: " + input);
		Gson gson = new Gson();
		Message incomingMessage = gson.fromJson(input, Message.class);
	    String messageType = incomingMessage.getType();
		String[] parts = input.split(" ");
		if (messageType.equals("updateAuctionItems")) {
			String itemsJson = gson.toJson(incomingMessage.getAuctionItems());
	        List<AuctionItem> newItems = gson.fromJson(itemsJson, new TypeToken<List<AuctionItem>>(){}.getType());
	        updateAuctionItems(newItems);
	    }
		if (parts.length == 3 && parts[0].equals("bid")) {
			int itemId = Integer.parseInt(parts[1]);
			String bidAmount = parts[2];
			boolean validBid = isValidBid(itemId, bidAmount);
			// if (validBid) {
			// 	// Notify all clients of new highest bid
			// 	setChanged();
			// 	notifyObservers(gson.toJson(new Message("newBid", "", itemId)));
			// 	// Update the corresponding AuctionItem with the new bid amount
			// 	for (AuctionItem item : items) {
			// 		if (item.getAuctionItemId() == itemId) {
			// 			//item.setHighestBidder(clientHandler.getClientId());
			// 			item.setHighestBid(String.valueOf(bidAmount));
			// 			break;
			// 		}
			// 	}
			// }
			// Send response to client
			Message outputMessage = new Message("output", Boolean.toString(validBid), 0);
			clientHandler.sendToClient(outputMessage);
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
			clientHandler.sendToClient(outputMessage);
		} 
		else {
			String output = "Error: ";
			clientHandler.sendToClient(new Message("output", output, 0));
		}
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
	private synchronized boolean isValidBid(int itemId, String bidAmount) {
		for (AuctionItem item : items) {
			if (item.getAuctionItemId() == itemId) {
				// Check if auction is still open
				if (item.closed()) {
					return false;
				}
				return item.bid(item.getHighestBidderId(), bidAmount);
			}
		}
		// Item not found
		return false;
	}
	public interface AuctionItemObserver {
		void onUpdate(AuctionItem item);
	}
	public List<AuctionItem> getAuctionItems() {
		return items;
	}

	public synchronized void addClient(ClientHandler client) {
	    clientHandlers.add(client);
	}

	public synchronized void removeClient(ClientHandler client) {
	    clientHandlers.remove(client);
	}

}
