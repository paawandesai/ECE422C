package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import server.AuctionItem;
import server.Bid;

public class BidHistoryUI extends BorderPane {
    private ListView<Bid> listView;
    private AuctionItem auctionItem;

    public BidHistoryUI(AuctionItem auctionItem) {
        this.auctionItem = auctionItem;

        Label titleLabel = new Label("Bid History for " + auctionItem.getName());
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label auctionLabel = new Label("Auction: " + auctionItem.getName());
        Label itemIdLabel = new Label("Item ID: " + auctionItem.getAuctionItemId());

        HBox labelsBox = new HBox(auctionLabel, itemIdLabel);
        labelsBox.setSpacing(10);

        VBox topBox = new VBox(titleLabel, labelsBox);
        topBox.setSpacing(10);
        topBox.setPadding(new Insets(10));

        listView = new ListView<>();
        listView.setPrefHeight(200);

        VBox centerBox = new VBox(listView);
        centerBox.setPadding(new Insets(10));

        setTop(topBox);
        setCenter(centerBox);

        updateBidHistory();
    }

    public void reset() {
        listView.getItems().clear();
    }

    public void showBidHistory() {
        updateBidHistory();
    }

    public void refresh() {
        listView.refresh();
    }
    public BorderPane getUI() {
        return this;
    }


    private void updateBidHistory() {
        ObservableList<Bid> bidHistory = FXCollections.observableArrayList(auctionItem.getAuction().getBidHistory());
        listView.setItems(bidHistory);
    }
}
