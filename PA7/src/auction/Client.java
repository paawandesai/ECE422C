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
	private AuctionWindow auctionWindow;
	List<AuctionItem> auctionItems = new List<AuctionItem>();

	public void main(String[] args) {
		try {
			new Client().setUpNetworking();
			javafx.application.Application.launch(LoginWindow.class);
			
			Message getItems = new Message(MessageType.GET_AUCTION_ITEMS);
			sendToServer(getItems);

			


			run();

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
	}

	public static void run() {
		while (true) {


		}
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

}
