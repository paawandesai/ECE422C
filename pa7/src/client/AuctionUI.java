package client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.AuctionItem;
import server.Bid;

import java.util.ArrayList;
import java.util.List;

public class AuctionUI {
    private final ClientGUI clientGUI;
    private final BorderPane auctionPane;
    private final List<ItemUI> itemUIList;
    private final ClientBackend clientBackend;
    private final VBox auctionList;

    public AuctionUI(ClientGUI clientGUI, ClientBackend clientBackend, VBox auctionList) {
        this.clientGUI = clientGUI;
        this.clientBackend = clientBackend;
        this.auctionList = auctionList;

        // Create the item UI list
        itemUIList = new ArrayList<>();

        // Create the auction pane
        auctionPane = new BorderPane();
        auctionPane.setPadding(new Insets(10));
        auctionPane.setPrefSize(600, 400);

        // Create the grid pane for item UIs
        GridPane itemGridPane = new GridPane();
        itemGridPane.setPadding(new Insets(10));
        itemGridPane.setHgap(10);
        itemGridPane.setVgap(10);

        /* Create the refresh button
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> refreshItems());

        // Add the refresh button to the top HBox
        HBox topHBox = new HBox(refreshButton);
        topHBox.setAlignment(Pos.CENTER_RIGHT);
        topHBox.setPadding(new Insets(10));
        */

        // Add the item grid pane to the center VBox
        VBox centerVBox = new VBox(itemGridPane);
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setPadding(new Insets(10));

        // Add the top HBox and center VBox to the auction pane
        //auctionPane.setTop(topHBox);
        auctionPane.setCenter(centerVBox);

        // Get the items from the server and add them to the item UI list
        clientBackend.getItems().forEach(this::addItemUI);
    }

    public BorderPane getAuctionPane() {
        return auctionPane;
    }
    

    public void refreshItems() {
        // Clear the item UI list and the item grid pane
        itemUIList.clear();
        ((GridPane) auctionPane.getCenter()).getChildren().clear();

        // Get the items from the server and add them to the item UI list
        clientBackend.getItems().forEach(this::addItemUI);
    }

    public void addItemUI(AuctionItem item) {
        // Create a new ItemUI and add it to the item UI list
        ItemUI itemUI = new ItemUI(item, clientBackend);
        itemUIList.add(itemUI);

        // Get the row and column indices for the item UI
        int row = (itemUIList.size() - 1) / 2;
        int col = (itemUIList.size() - 1) % 2;

        // Add the item UI to the item grid pane
        GridPane itemGridPane = (GridPane) auctionPane.getCenter();
        itemGridPane.add(itemUI.getItemPane(), col, row);
    }


    public void showAuctions() {
        Platform.runLater(() -> {
            auctionList.getChildren().clear();
            List<AuctionItem> items = clientBackend.getItems();
            for (AuctionItem item : items) {
                auctionList.getChildren().add(new ItemUI(item, clientBackend).getItemPane());
            }
        });
    }

    public void bidPlaced(String itemName, Bid bid, String bidder) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Bid Placed");
            alert.setHeaderText("Bid Placed Successfully");
            alert.setContentText(String.format("Congratulations %s, your bid of %.2f for %s was successful!", bidder, bid.getAmount(), itemName));
            alert.showAndWait();
        });
    }

    public void invalidBid(String itemName, Bid bid, String bidder) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Invalid Bid");
            alert.setHeaderText("Bid Rejected");
            alert.setContentText(String.format("Sorry %s, your bid of %.2f for %s is invalid. Please enter a valid bid amount.", bidder, bid.getAmount(), itemName));
            alert.showAndWait();
        });
    }

    public void bidTooLow(String itemName, Bid bid, String bidder) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Bid Too Low");
            alert.setHeaderText("Bid Rejected");
            alert.setContentText(String.format("Sorry %s, your bid of %.2f for %s is too low. Please enter a higher bid amount.", bidder, bid.getAmount(), itemName));
            alert.showAndWait();
        });
    }

    public void itemNotFound(String itemName) {
        Platform.runLater(() -> {
            Label label = new Label(String.format("Item %s not found.", itemName));
            label.setPadding(new Insets(10));
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 14pt");
            auctionList.getChildren().clear();
            auctionList.getChildren().add(label);
            auctionList.setAlignment(Pos.CENTER);
        });
    }
    public void showBidHistory(String itemName, List<Bid> bidHistory) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Bid History for " + itemName);
            alert.setHeaderText("Bid history for " + itemName + ":");
            StringBuilder contentText = new StringBuilder();
            for (Bid bid : bidHistory) {
                contentText.append("\n\nBid amount: ").append(bid.getAmount())
                           .append("\nBidder: ").append(bid.getBidderName());
            }
            alert.setContentText(contentText.toString());
            alert.showAndWait();
        });
    }
    public void addItem(String name, String description) {
        // Create a new AuctionItem object
        AuctionItem item = new AuctionItem(name, 0, description, description);

        // Add the item to the server using the ClientBackend instance
        boolean success = clientBackend.addItem(item);

        if (success) {
            // Add the item to the item UI list
            addItemUI(item);

            // Show a success message to the user
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Item Added");
            alert.setHeaderText("Item Added Successfully");
            alert.setContentText("The item has been added to the auction.");
            alert.showAndWait();
        } else {
            // Show an error message to the user
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Item Not Added");
            alert.setHeaderText("Item Not Added");
            alert.setContentText("There was an error adding the item to the auction.");
            alert.showAndWait();
        }
    }
    public void showError(String message) {
        System.out.println("Error: " + message);
    }
    
    public void showItems(List<AuctionItem> items) {
        if (items.isEmpty()) {
            System.out.println("No items to show.");
        } else {
            System.out.println("Items for auction:");
            for (AuctionItem item : items) {
                System.out.println("- " + item.getName() + " (ID: " + item.getAuctionItemId() + ")");
            }
        }
    }
    

}

