package auction;

import java.awt.TrayIcon.MessageType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Observer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import auction.Server.AuctionItemObserver;
import javafx.application.Platform;

import java.util.List;
import java.util.Observable;

class ClientHandler implements Runnable, Observer {

	private Server server;
	private Socket clientSocket;
	BufferedReader fromClient;
	PrintWriter toClient;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	protected ClientHandler(Server server, Socket clientSocket) throws IOException {
		this.server = server;
		this.clientSocket = clientSocket;
		fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		toClient = new PrintWriter(this.clientSocket.getOutputStream());

	}

	@Override
	public void run() {
		System.out.println("[CH]inside run()");
		String input;
		try {
			while ((input = fromClient.readLine()) != null) {
				System.out.println("[ch] From client: " + input);
				server.processRequest(input, this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[CH]done with run()");

	}
	
	protected void sendMessageToClient(Message message) {
		System.out.println("[ch] Sending to client: " + message);
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		toClient.println(gson.toJson(message));
		toClient.flush();
	}
	protected void sendToClient(String string) {
		System.out.println("[ch] Sending to client: " + string);
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		toClient.println(gson.toJson(string));
		toClient.flush();

	}

	@Override
	public void update(Observable o, Object arg) {
		this.sendToClient((String) arg);
	}

}