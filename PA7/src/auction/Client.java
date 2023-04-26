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
	private AuctionWindow auctionWindow;

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
		Socket socket = new Socket(host, 4276);
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
						sendToServer(request);
					} else {
						Message request = new Message(input);
						sendToServer(request);
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

		if (message.type.equals("UPDATE_AUCTION_ITEMS")) {
			AuctionItem updatedItem = gson.fromJson(input, AuctionItem.class);
			auctionWindow.updateAuctionItem(updatedItem);
		}

	}
	protected void updateAuctionItem(AuctionItem auctionItem) {
		if (auctionItem != null) {
			for (AuctionItem item : auctionWindow.getTableView().getItems()) {
				if (item.getAuctionItemId().equals(auctionItem.getAuctionItemId())) {
					item.setName(auctionItem.getName());
					item.setDescription(auctionItem.getDescription());
					item.setStartPrice(auctionItem.getStartPrice());
					item.setHighestBid(auctionItem.getHighestBid());
					auctionWindow.getTableView().refresh();
					break;
				}
			}
		}
	}

	protected void sendToServer(Message message) {
		System.out.println("Sending to server: " + message);
		toServer.println(message);
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
	public boolean updateBid(String itemId, Double newBid) {
		// Construct the message to update the bid for the given auction item
		Message message = new Message();
		message.type = "bid";
		message.itemId = itemId;
		message.bidAmount = newBid;
		// Send the message to the server
		sendToServer(message);
		return true;

	}


}
