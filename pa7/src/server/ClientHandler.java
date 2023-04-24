package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    @Override
    public void run() {
        try {
            // Prompt the client to enter a username
            out.println("Enter your username:");
            username = in.readLine();

            // Add the client to the server's list of active clients
            server.addClient(this);

            // Loop to read and process commands from the client
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] tokens = inputLine.split("\\s+");
                String command = tokens[0];

                switch (command) {
	                case "BID":
	                    if (tokens.length < 3) {
	                        out.println("Invalid command. Usage: BID [item name] [amount]");
	                        break;
	                    }
	                    String itemName = tokens[1];
	                    double amount;
	                    try {
	                        amount = Double.parseDouble(tokens[2]);
	                    } catch (NumberFormatException e) {
	                        out.println("Invalid bid amount.");
	                        break;
	                    }
	                    Bid bid = new Bid(username, amount);
	                    server.placeBid(this, itemName, bid);
	                    break;

                    case "LIST":
                        server.listItems(this);
                        break;

                    case "LOGOUT":
                        server.removeClient(this);
                        socket.close();
                        return;

                    default:
                        out.println("Invalid command. Valid commands: BID, LIST, LOGOUT");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e);
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e);
            }
        }
    }

    // Send a message to this client
    public void sendMessage(String message) {
        out.println(message);
    }

    // Get the username of this client
    public String getUsername() {
        return username;
    }
    public void send(String message) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e);
        }
    }


}
