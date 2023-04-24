package client;

import javafx.geometry.Insets;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import server.AuctionItem;
import server.Bid;

public class ItemUI extends VBox {
    private AuctionItem item;
    private Label nameLabel;
    private Label descriptionLabel;
    private Label currentBidLabel;
    private Label highestBidderLabel;
    private Label timeLeftLabel;
    private TextField bidField;
    private Button bidButton;
    private Button bidHistoryButton;
    private ClientBackend backend;

    public ItemUI(AuctionItem item, ClientBackend backend) {
        this.item = item;
        this.backend = backend;

        setSpacing(10);

        nameLabel = new Label(item.getAuctionItemId());
        Label itemNameLabel = new Label(item.getName());
        itemNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        getChildren().addAll(new Label("Item Name: "), nameLabel, itemNameLabel);

        descriptionLabel = new Label(item.getDescription());
        Label itemDescLabel = new Label(item.getDescription());
        itemDescLabel.setWrapText(true);
        getChildren().addAll(new Label("Description: "), descriptionLabel, itemDescLabel);

        currentBidLabel = new Label(String.valueOf(item.getAuction().getHighestBid()));
        Label currentBidLabelTitle = new Label("Current Bid: ");
        currentBidLabel.setStyle("-fx-font-size: 14;");
        getChildren().addAll(currentBidLabelTitle, currentBidLabel);

        highestBidderLabel = new Label(item.getAuction().getHighestBidder());
        Label highestBidderLabelTitle = new Label("Highest Bidder: ");
        getChildren().addAll(highestBidderLabelTitle, highestBidderLabel);

        timeLeftLabel = new Label(String.valueOf(item.getAuction().getRemainingTime()));
        Label timeLeftLabelTitle = new Label("Time Left: ");
        getChildren().addAll(timeLeftLabelTitle, timeLeftLabel);

        bidField = new TextField();
        getChildren().addAll(new Label("Place Bid: "), bidField);

        bidButton = new Button("Bid");
        bidButton.setOnAction(event -> {
            try {
                double amount = Double.parseDouble(bidField.getText());
                Bid bid = new Bid(backend.getUsername(), amount);
                backend.placeBid(item.getAuctionItemId(), bid);
                bidField.setText("");
                refreshItem();
            } catch (NumberFormatException ex) {
                ClientGUI.showError("Invalid bid amount");
            }
        });

        bidHistoryButton = new Button("Bid History");
        bidHistoryButton.setOnAction(event -> ClientGUI.showBidHistory());

        getChildren().addAll(new Label(), bidHistoryButton);
    }

    public void update() {
        currentBidLabel.setText(String.valueOf(item.getAuction().getHighestBid()));
        highestBidderLabel.setText(item.getAuction().getHighestBidder());
        timeLeftLabel.setText(String.valueOf(item.getAuction().getRemainingTime()));
    }
    public GridPane getItemPane() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label("Item Name:");
        gridPane.add(nameLabel, 0, 0);

        Label itemNameLabel = new Label(item.getName());
        itemNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        gridPane.add(itemNameLabel, 1, 0);

        Label descriptionLabel = new Label("Description:");
        gridPane.add(descriptionLabel, 0, 1);

        Label itemDescLabel = new Label(item.getDescription());
        itemDescLabel.setWrapText(true);
        gridPane.add(itemDescLabel, 1, 1);

        Label currentBidLabel = new Label("Current Bid:");
        gridPane.add(currentBidLabel, 0, 2);

        Label currentBidValueLabel = new Label(String.valueOf(item.getAuction().getHighestBid()));
        currentBidValueLabel.setStyle("-fx-font-size: 14;");
        gridPane.add(currentBidValueLabel, 1, 2);

        Label highestBidderLabel = new Label("Highest Bidder:");
        gridPane.add(highestBidderLabel, 0, 3);

        Label highestBidderValueLabel = new Label(item.getAuction().getHighestBidder());
        gridPane.add(highestBidderValueLabel, 1, 3);

        Label timeLeftLabel = new Label("Time Left:");
        gridPane.add(timeLeftLabel, 0, 4);

        Label timeLeftValueLabel = new Label(String.valueOf(item.getAuction().getRemainingTime()));
        gridPane.add(timeLeftValueLabel, 1, 4);

        TextField bidField = new TextField();
        gridPane.add(new Label("Place Bid:"), 0, 5);
        gridPane.add(bidField, 1, 5);

        Button bidButton = new Button("Bid");
        bidButton.setOnAction(event -> {
            try {
                double amount = Double.parseDouble(bidField.getText());
                Bid bid = new Bid(backend.getUsername(), amount);
                backend.placeBid(item.getAuctionItemId(), bid);
                bidField.setText("");
                update();
            } catch (NumberFormatException ex) {
                ClientGUI.showError("Invalid bid amount");
            }
        });

        Button bidHistoryButton = new Button("Bid History");
        bidHistoryButton.setOnAction(event -> ClientGUI.showBidHistory());

        gridPane.add(bidButton, 1, 6);
        gridPane.add(bidHistoryButton, 1, 7);

        return gridPane;
    }
    public Pane getUI() {
        // Create the UI elements
        Label itemLabel = new Label("Item: " + item.getName());
        Label descriptionLabel = new Label("Description: " + item.getDescription());
        Label timeLabel = new Label("Time left: " + item.getTimeLeft() + " seconds");
        Label highBidLabel = new Label("High Bid: " + item.getCurrentBid());

        // Create a VBox to hold the UI elements
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getChildren().addAll(itemLabel, descriptionLabel, timeLabel, highBidLabel);

        // Create a HBox to hold the bid button and bid text field
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        Button bidButton = new Button("Place Bid");
        TextField bidTextField = new TextField();
        hBox.getChildren().addAll(bidTextField, bidButton);

        // Add an event handler to the bid button
        bidButton.setOnAction(event -> {
            try {
                double bidAmount = Double.parseDouble(bidTextField.getText());
                Bid newBid = new Bid(backend.getUsername(), bidAmount);
                backend.placeBid(item.getAuctionItemId(), newBid);
                bidTextField.clear();
            } catch (NumberFormatException e) {
                System.out.println("Invalid bid amount");
            }
        });

        // Add the HBox to the VBox
        vBox.getChildren().add(hBox);

        return vBox;
    }
    public void reset() {
        // Clear the bid field
        bidField.setText("");

        // Reset the labels to their default values
        currentBidLabel.setText(String.valueOf(item.getCurrentBid()));
        highestBidderLabel.setText("");
        timeLeftLabel.setText(String.valueOf(item.getTimeLeft()));

        // Reset the item's auction
        item.resetAuction();
    }



    public void refreshItem() {
        backend.refreshAuctionItem(item.getName(), item.getDescription());
    }
}
