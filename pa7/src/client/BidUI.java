package client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import server.AuctionItem;
import server.Bid;
import server.BidValidator;

public class BidUI extends BorderPane {
    private AuctionItem auctionItem;
    private TextField bidderTextField;
    private TextField bidAmountTextField;
    private Label errorLabel;

    public BidUI(AuctionItem auctionItem) {
        this.auctionItem = auctionItem;

        Label titleLabel = new Label("Place a Bid for " + auctionItem.getName());
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label auctionLabel = new Label("Auction: " + auctionItem.getName());
        Label itemIdLabel = new Label("Item ID: " + auctionItem.getAuctionItemId());

        HBox labelsBox = new HBox(auctionLabel, itemIdLabel);
        labelsBox.setSpacing(10);

        VBox topBox = new VBox(titleLabel, labelsBox);
        topBox.setSpacing(10);
        topBox.setPadding(new Insets(10));

        Label bidderLabel = new Label("Bidder Name:");
        bidderTextField = new TextField();

        Label bidAmountLabel = new Label("Bid Amount:");
        bidAmountTextField = new TextField();

        HBox inputBox1 = new HBox(bidderLabel, bidderTextField);
        inputBox1.setSpacing(10);
        inputBox1.setAlignment(Pos.CENTER);

        HBox inputBox2 = new HBox(bidAmountLabel, bidAmountTextField);
        inputBox2.setSpacing(10);
        inputBox2.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(inputBox1, inputBox2);
        centerBox.setSpacing(10);
        centerBox.setPadding(new Insets(10));

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        VBox bottomBox = new VBox(errorLabel);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));

        setTop(topBox);
        setCenter(centerBox);
        setBottom(bottomBox);

        setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    handlePlaceBid();
                    break;
                case ESCAPE:
                    reset();
                    break;
                default:
                    break;
            }
        });
    }

    private void handlePlaceBid() {
        String bidderName = bidderTextField.getText();
        String bidAmountText = bidAmountTextField.getText();
        double bidAmount = 0.0;

        try {
            bidAmount = Double.parseDouble(bidAmountText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid bid amount.");
            return;
        }

        Bid bid = new Bid(bidderName, bidAmount);

        if (!BidValidator.isBidValid(auctionItem, bid, auctionItem.getAuction().getAllBids())) {
            errorLabel.setText("Invalid bid.");
            return;
        }

        auctionItem.getAuction().setHighestBid(bidderName, bidAmount);

        errorLabel.setText("Bid placed successfully.");

        reset();
    }

    void reset() {
        bidderTextField.setText("");
        bidAmountTextField.setText("");
        errorLabel.setText("");
    }
    

    public BorderPane getUI() {
        return this;
    }
}
