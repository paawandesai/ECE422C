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

import auction.Server.AuctionItemObserver;
import javafx.application.Platform;

import java.util.List;
import java.util.Observable;

class ClientHandler implements Runnable, Observer, AuctionItemObserver {

	private Server server;
	private Socket clientSocket;
	private BufferedReader fromClient;
	private PrintWriter toClient;
	private ObjectOutputStream out;
	private ObjectInputStream in;

	protected ClientHandler(Server server, Socket clientSocket) throws IOException {
		this.server = server;
		this.clientSocket = clientSocket;
		out = new ObjectOutputStream(clientSocket.getOutputStream());
		in = new ObjectInputStream(clientSocket.getInputStream());

		fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		toClient = new PrintWriter(this.clientSocket.getOutputStream());

	}

	public void sendToClient(Message message) {
		System.out.println("Sending to client: " + message);
		toClient.println(message);
		toClient.flush();
	}

	@Override
	public void run() {
		String input;
		try {
			while ((input = fromClient.readLine()) != null) {
				System.out.println("[CH]: " + input);
				server.processRequest(input, this);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// @Override
	// public void onUpdate(AuctionItem item) {
	// 	// TODO Auto-generated method stub

	// }

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}

}