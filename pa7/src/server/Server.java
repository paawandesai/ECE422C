package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import other.AuctionConstants;

public class Server {
    private static ServerSocket serverSocket;
    private static List<ClientHandler> clientHandlers;
    private static List<AuctionItem> items;
    private static ClientHandler handler;
	private int portNumber = AuctionConstants.SERVER_PORT;

    public Server(int portNumber) {
        this.portNumber = portNumber;
        Server.clientHandlers = new ArrayList<>();
        Server.items = new ArrayList<>();
    }

    public void start() {
        try {
        	
            serverSocket = new ServerSocket(AuctionConstants.SERVER_PORT);
            System.out.println("Server started on port " + AuctionConstants.SERVER_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, this);
                clientHandlers.add(handler);
                handler.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static synchronized void logout() {
        for (ClientHandler handler : clientHandlers) {
            handler.send("Server is shutting down. Goodbye!");
            handler.close();
        }
        clientHandlers.clear();
        items.clear();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public synchronized void placeBid(ClientHandler handler, String itemName, Bid bid) {
        AuctionItem item = null;
        for (AuctionItem i : items) {
            if (i.getAuctionItemId().equals(itemName)) {
                item = i;
                break;
            }
        }
        if (item == null) {
            handler.send("Item not found.");
            return;
        }
        if (item.getAuction().getAllBids().isEmpty() || item.getAuction().getHighestBid() < bid.getAmount()) {
            if (BidValidator.isBidValid(item, bid, item.getAuction().getAllBids())) {
                item.getAuction().placeBid(itemName, bid, item.getAuction().getAllBids());
                sendToAllClients("Bid of " + bid.getAmount() + " has been placed on item " + item.getAuctionItemId());
                sendToAllClients("Current highest bid on item " + item.getAuctionItemId() + " is " + item.getAuction().getHighestBid());
            } else {
                handler.send("Invalid bid.");
            }
        } else {
            handler.send("Bid must be higher than current highest bid.");
        }
    }

    public synchronized void listItems(ClientHandler handler) {
        if (items.isEmpty()) {
            handler.send("No items in auction.");
        } else {
     
        	for (AuctionItem item : items) {
                handler.send(item.getAuctionItemId() + ": " + item.getAuction() + ", Reserve Price: " + item.getReservePrice());
            }
        }
    }
    public synchronized List<AuctionItem> getItems() {
        return items;
    }
    public synchronized AuctionItem getItem(String itemName) {
        for (AuctionItem item : items) {
            if (item.getAuctionItemId().equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    public synchronized void removeClient(ClientHandler handler) {
        clientHandlers.remove(handler);
    }

    public synchronized void addClient(ClientHandler handler) {
        clientHandlers.add(handler);
    }

    public synchronized void sendToAllClients(String message) {
        for (ClientHandler handler : clientHandlers) {
            handler.send(message);
        }
    }

    public void addItem(AuctionItem item) {
        items.add(item);
        item.setAuction(new Auction(item));
    }
}
