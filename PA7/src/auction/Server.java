/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* <Paawan Desai>
* <pkd397>
* <17140>
* Spring 2023
*/
package auction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

class Server extends Observable {
	public static List<AuctionItem> auctionItems;
	private List<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
	private ObjectOutputStream out;
	private ObjectInputStream in;

	public Server() {
		auctionItems = readItemsFromFile();

	}


	public static void main(String[] args) {
		Server server = new Server();
		System.out.println(server.auctionItems);
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

	private synchronized void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4280);
		while(true) {
			Socket clientSocket = serverSock.accept();
			System.out.println("[server] new client has connected: " + clientSocket);
			ClientHandler handler = new ClientHandler(this, clientSocket);
			this.addClient(handler);
			Thread t = new Thread(handler);
			t.start();
		}
			
	}
		

	protected void processRequest(String input, ClientHandler handler) {
		System.out.println("[server] got input: " + input);
		Gson gson = new Gson();
		Message incomingMessage = gson.fromJson(input, Message.class);
		System.out.println("[server] incoming message: " + incomingMessage);
//		if (incomingMessage.messageType == Message.updateAuctionItems) {
//			String returnAuctionItemMsg = gson.toJson(input, Message.class);
//			handler.sendToClient(returnAuctionItemMsg);
//		update master list, and send to clientHandler, and iterate through client handler list for every client
//		another background thread in every auction window
//		}
//		for MessageType.GET_AUCTION_ITEMS
		if (incomingMessage.messageType == Message.getAuctionItems) {
			String getAuctionItemsList = gson.toJson(auctionItems);
			System.out.println("[server] jsonified auction item for GET_AUCTION_ITEMS "+ getAuctionItemsList);
			Message ret = new Message(MessageType.SEND_AUCTION_ITEMS, getAuctionItemsList);
			handler.sendMessageToClient(ret);
		}
//		handle UPDATE_AUCTION_BID
		else if(incomingMessage.messageType == Message.updateAuctionBid) {
//			replace auctionItems with this new list
			auctionItems = incomingMessage.auctionItemsList;
			String sendUpdatedAuctionBid = gson.toJson(auctionItems);
			Message ret = new Message(MessageType.UPDATE_AUCTION_BID, sendUpdatedAuctionBid);
			System.out.println("[server] jsonified auction item list for UPDATE_AUCTION_BID "+ sendUpdatedAuctionBid);	
//			for handler in clientHandlers, sesndMessageToClient of type SEND_AUCTION_ITEMS and this new updated list
			for(int i=0;i<clientHandlers.size();i++) {
				clientHandlers.get(i).sendMessageToClient(ret);
			}
		}
//		TODO PAAWAN
//		handle UPDATE_AUCTION_BID
//		replace auctionItems with this new list
//		for handler in clientHandlers, sendMessageToClient of type SEND_AUCTION_ITEMS and this new updated list

	}


	static List<AuctionItem> readItemsFromFile() {
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
		for (AuctionItem item : auctionItems) {
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
		return auctionItems;
	}

	public synchronized void addClient(ClientHandler client) {
	    clientHandlers.add(client);
	}

	public synchronized void removeClient(ClientHandler client) {
	    clientHandlers.remove(client);
	}

}