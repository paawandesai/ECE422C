package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


import javax.xml.ws.Response;

import org.omg.CORBA.Request;

import server.AuctionItem;
import server.Bid;
import server.ClientHandler;
import server.Server;
import other.AuctionConstants;

public class ClientBackend {

    private String serverAddress = AuctionConstants.SERVER_ADDRESS;
    private int serverPort = AuctionConstants.SERVER_PORT;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ObjectInputStream in1;
    private ObjectOutputStream out1;
    private String username;
    private AuctionUI auctionUI;
    private List<AuctionItem> items;

    public ClientBackend(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
    public ClientBackend(Socket socket) {
        this.socket = socket;
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Connected to server");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAuctionUI(AuctionUI auctionUI) {
        this.auctionUI = auctionUI;
    }

    public List<AuctionItem> listItems() {
        out.println("LIST_ITEMS");
        String response;
        try {
            while ((response = in.readLine()) != null) {
                if (response.equals("END_LIST_ITEMS")) {
                    break;
                }
                String[] parts = response.split(": ");
                String itemName = parts[0];
                String itemDetails = parts[1];
                AuctionItem item = new AuctionItem(itemName, 0, username, itemDetails);
                addItem(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean addItem(AuctionItem item) {
        items.add(item);
        auctionUI.addItem(item.getName(), item.getDescription());
        return true;
    }

    public void refreshAuctionItem(String itemName, String itemDetails) {
        AuctionItem item = new AuctionItem(itemName, 0, username, itemDetails);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(item.getName())) {
                items.set(i, item);
                auctionUI.refreshItems();
                break;
            }
        }
    }

    public void placeBid(String itemName, Bid bid) {
        out.println("PLACE_BID " + itemName + " " + bid + " " + username);
        String response;
        try {
            while ((response = in.readLine()) != null) {
                if (response.equals("BID_PLACED")) {
                    auctionUI.bidPlaced(itemName, bid, username);
                    break;
                } else if (response.equals("INVALID_BID")) {
                    auctionUI.invalidBid(itemName, bid, username);
                    break;
                } else if (response.equals("BID_TOO_LOW")) {
                    auctionUI.bidTooLow(itemName, bid, username);
                    break;
                } else if (response.equals("ITEM_NOT_FOUND")) {
                    auctionUI.itemNotFound(itemName);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getBidHistory(String itemName) {
        out.println("GET_BID_HISTORY " + itemName);
        String response;
        try {
            List<Bid> bidHistory = new ArrayList<>();
            while ((response = in.readLine()) != null) {
                if (response.equals("END_BID_HISTORY")) {
                    break;
                }
                String[] parts = response.split(": ");
                String bidderName = parts[0];
                int bidAmount = Integer.parseInt(parts[1]);
                bidHistory.add(new Bid(bidderName, bidAmount));
            }
            auctionUI.showBidHistory(itemName, bidHistory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean login(String username, String password) {
        try {
            // Establish connection with server
            Socket socket = new Socket(serverAddress, serverPort);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // Send login credentials to server
            out.writeUTF("login:" + username + ":" + password);
            out.flush();

            // Wait for server response
            String response = in.readUTF();

            // Check if login was successful
            if (response.equals("login_successful")) {
                // Save client's username and socket
                this.username = username;
                this.socket = socket;
                return true;
            } else {
                // Close socket and return false
                socket.close();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

	public String getUsername() {
		return username;
	}
	public List<AuctionItem> getItems() {
	    List<AuctionItem> items = new ArrayList<>();

	    try {
	        // Connect to the server
	        Socket socket = new Socket(serverAddress, serverPort);

	        // Create the input and output streams
	        ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
	        ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());

	        // Send the request for items
	        outStream.writeObject("getItems");

	        // Receive the list of items from the server
	        Object obj = inStream.readObject();
	        if (obj instanceof List<?>) {
	            List<?> itemList = (List<?>) obj;
	            for (Object itemObj : itemList) {
	                if (itemObj instanceof AuctionItem) {
	                    AuctionItem item = (AuctionItem) itemObj;
	                    items.add(item);
	                }
	            }
	        }

	        // Close the streams and socket
	        inStream.close();
	        outStream.close();
	        socket.close();

	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }

	    return items;
	}

	

}
