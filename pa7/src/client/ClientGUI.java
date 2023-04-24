package client;

import java.util.Collection;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import server.AuctionItem;
import server.Server;

public class ClientGUI extends Application {
    private ItemUI itemUI;
    private BidUI bidUI;
    private static BidHistoryUI bidHistoryUI;
    private LoginUI loginUI;
    private AuctionItem item;
    private ClientBackend backend;
    private AuctionUI auctionUI;

    @Override
    public void start(Stage primaryStage) {
        itemUI = new ItemUI(item, backend);
        bidUI = new BidUI(item);
        bidHistoryUI = new BidHistoryUI(item);
        loginUI = new LoginUI(backend, auctionUI);

        // Set up UI components
        TabPane tabPane = new TabPane();
        Tab itemTab = new Tab("Item");
        Tab bidTab = new Tab("Bid");
        Tab bidHistoryTab = new Tab("Bid History");
        tabPane.getTabs().addAll(itemTab, bidTab, bidHistoryTab);
        
        VBox itemBox = new VBox(10);
        itemBox.setAlignment(Pos.CENTER);
        itemBox.getChildren().addAll(itemUI.getUI());
        itemTab.setContent(itemBox);
        
        VBox bidBox = new VBox(10);
        bidBox.setAlignment(Pos.CENTER);
        bidBox.getChildren().addAll(bidUI.getUI());
        bidTab.setContent(bidBox);
        
        VBox bidHistoryBox = new VBox(10);
        bidHistoryBox.setAlignment(Pos.CENTER);
        bidHistoryBox.getChildren().addAll(bidHistoryUI.getUI());
        bidHistoryTab.setContent(bidHistoryBox);
        
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.getChildren().addAll((Collection<? extends Node>) loginUI.getUI());
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab == bidHistoryTab) {
                bidHistoryUI.refresh();
            }
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            Server.logout();
            loginUI.reset();
            itemUI.reset();
            bidUI.reset();
            bidHistoryUI.reset();
            tabPane.getSelectionModel().selectFirst();
        });
        HBox.setMargin(logoutButton, new Insets(10));
        HBox logoutBox = new HBox(logoutButton);
        logoutBox.setAlignment(Pos.CENTER_RIGHT);

        HBox.setHgrow(tabPane, Priority.ALWAYS);
        VBox mainBox = new VBox(tabPane, logoutBox);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // Set up stage
        Scene scene = new Scene(mainBox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Auction Client");
        primaryStage.show();
    }
    public static void showError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
    public static void showBidHistory() {
        bidHistoryUI.showBidHistory();
    }
}
