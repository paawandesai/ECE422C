package client;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import javafx.scene.layout.VBox;
import other.AuctionConstants;
import server.AuctionItem;

public class Client {
    private static String username;
    private static ClientGUI clientGUI = new ClientGUI();
    private static AuctionUI auctionUI;
    private static ClientBackend clientBackend;
    private static VBox auctionList = new VBox();

    public static void main(String[] args) {
 
        try {
        	Socket socket = new Socket(AuctionConstants.SERVER_ADDRESS, AuctionConstants.SERVER_PORT);
            clientBackend = new ClientBackend(socket);
            auctionUI = new AuctionUI(clientGUI, clientBackend, auctionList);
            clientBackend.setAuctionUI(auctionUI);

            // Read in initial list of items from server
            List<AuctionItem> items = clientBackend.getItems();
            auctionUI.showItems(items);

            // Start client backend thread to handle server communication
            new Thread().start();

            // Show auction UI
            auctionUI.showAuctions();

        } catch (IOException e) {
            System.err.println("Could not connect to server.");
            e.printStackTrace();
        }
    }

    public static String getUsername() {
        return username;
    }

    public static AuctionUI getAuctionUI() {
        return auctionUI;
    }

    public static void showError(String message) {
        auctionUI.showError(message);
    }

    public static void showBidHistory(AuctionItem item) {
        BidHistoryUI bidHistoryUI = new BidHistoryUI(item);
        bidHistoryUI.showBidHistory();
    }
}
