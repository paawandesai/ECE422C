/*
 * EE422C Final Project submission by
 * Replace <...> with your actual data.
 * <Paawan Desai>
 * <pkd397>
 * <17140>
 * Spring 2023
 * Slip Day Used: 1
 */
package auction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.application.Platform;
import javafx.stage.Stage;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;


class Client {

	private static String host = "127.0.0.1";
	private BufferedReader fromServer;
	private static PrintWriter toServer;
	
	private BufferedReader fromAuctionWindow;
	private PrintWriter toAuctionWindow;
	
	private Scanner consoleInput = new Scanner(System.in);
	static List<AuctionItem> auctionItems;
	Client client;
	
	public Client() {
		client = this;
	}


	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket socket = new Socket(host, 4280);
		System.out.println("Connecting to... " + socket);
		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		toServer = new PrintWriter(socket.getOutputStream());
		
		
		

		Thread waiter = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					ServerSocket serverSock = new ServerSocket(4281);
					Socket clientSocket = serverSock.accept();
					System.out.println("Connecting to... " + clientSocket);
					toAuctionWindow = new PrintWriter(clientSocket.getOutputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}

				
			}
		});
		
		Thread readerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("[client][readerThread] inside run()");
				String input;
				try {
					while ((input = fromServer.readLine()) != null) {
						System.out.println("[client] From server: " + input);
						processRequest(input);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("[client][readerThread] done with run()");					
			}
		});

		readerThread.start();
		waiter.start();

	}

	protected void processRequest(String input) {
		System.out.println("[client] input received from CH"+input);
//		forward that input to the AW
		toAuctionWindow.println(input);
		toAuctionWindow.flush();
	}
	
	public static List<AuctionItem> getListFromServer() {
		System.out.println("[client] in getListFromServer, this is what prints" + auctionItems);
		return auctionItems;
	}
	
	protected static void updateAuctionItem(AuctionItem auctionItem) {
		if (auctionItem != null) {
			for (AuctionItem item : auctionItems) {
				if (item.getAuctionItemId().equals(auctionItem.getAuctionItemId())) {
					item.setHighestBid(auctionItem.getHighestBid());
					break;
				}
			}
		}
	}
	protected static void updateAuctionItemList(List<AuctionItem> auctionItem) {
		if (auctionItem != null) {
			for (int i=0;i<auctionItem.size();i++) {
				if (auctionItems.get(i).getAuctionItemId().equals(auctionItem.get(i).getAuctionItemId())) {
					auctionItem.set(i, auctionItem.get(i));
					break;
				}
			}
		}
	}
	protected static void sendToServer(Message message) {
//		being called with MessageType GET_AUCTION_ITEMS
		System.out.println("[client]Sending to server: " + message);
		updateAuctionItem(message.auctionItem);
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		toServer.println(gson.toJson(message));
		toServer.flush();
	}
	


	public static void main(String[] args) {
		try {
			Client client = new Client();
			client.setUpNetworking();		

			javafx.application.Application.launch(AuctionWindow.class);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error connecting to server. Please check if the server is running.");
		}
	}
}