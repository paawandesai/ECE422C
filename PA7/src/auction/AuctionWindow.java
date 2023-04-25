package auction;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AuctionWindow extends Application {

    private Server server;
    private Client client;

    private TextField bidAmountField;
    private TextField currentAuctionItemField;
    private Button bidButton;
    private ListView<AuctionItem> auctionItemsList;


    @Override
    public void start(Stage primaryStage) {
        // Initialize the server and client
        server = new Server();
        client = new Client();

        // Create the main layout for the auction window
    	// Get the list of auction items from the server
        List<AuctionItem> auctionItems = server.readItemsFromFile();
        TableView<AuctionItem> tableView = new TableView<>();

        // Start listening for updates from the server
        primaryStage.setTitle("Auction Window");

        VBox mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10, 10, 10, 10));
        mainLayout.setSpacing(10);
        
        //Item ID
        TableColumn<AuctionItem, String> idColumn = new TableColumn<>("Item ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("auctionItemId"));
        //Name
        TableColumn<AuctionItem, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        //Description
        TableColumn<AuctionItem, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        //Start Price
        TableColumn<AuctionItem, String> priceColumn = new TableColumn<>("Start Bid");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("startPrice"));
        // Create the current bid column
        TableColumn<AuctionItem, String> currentBidColumn = new TableColumn<>("Current Bid");
        currentBidColumn.setCellValueFactory(new PropertyValueFactory<>("highestBid"));
        currentBidColumn.setCellFactory(column -> {
            TableCell<AuctionItem, String> cell = new TableCell<AuctionItem, String>() {
                @Override
                protected void updateItem(String currentBid, boolean empty) {
                    super.updateItem(currentBid, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(String.format("$%.2f", currentBid));
                    }
                }
            };

            // Set a listener on the item property of the cell to update the current bid when it changes
            cell.itemProperty().addListener((obs, oldBid, newBid) -> {
                if (newBid != null) {
                    cell.setText(String.format("$%.2f", newBid));
                } else {
                    cell.setText(null);
                }
            });

            return cell;
        });
        tableView.getColumns().addAll(idColumn, nameColumn, descriptionColumn, priceColumn, currentBidColumn);
        tableView.getItems().addAll(auctionItems);
        mainLayout.getChildren().add(tableView);        
        
        // Add a label for the current auction item
        HBox currentAuctionItemBox = new HBox();
        currentAuctionItemBox.setSpacing(5);
        Label currentAuctionItemLabel = new Label("Choose Auction Item: ");
        currentAuctionItemBox.getChildren().add(currentAuctionItemLabel);
        currentAuctionItemField = new TextField();
        currentAuctionItemField.setPrefWidth(50);
        currentAuctionItemBox.getChildren().add(currentAuctionItemField);
        mainLayout.getChildren().add(currentAuctionItemBox);

        
        // Add a label and field for the bid amount
        HBox bidAmountBox = new HBox();
        bidAmountBox.setSpacing(5);
        Label bidAmountLabel = new Label("Bid Amount:");
        bidAmountBox.getChildren().add(bidAmountLabel);
        bidAmountField = new TextField();
        bidAmountField.setPrefWidth(50);
        bidAmountBox.getChildren().add(bidAmountField);
        mainLayout.getChildren().add(bidAmountBox);

        // Add a button for placing a bid
        bidButton = new Button("Place Bid");
        bidButton.setDisable(false);
        mainLayout.getChildren().add(bidButton);
        bidButton.setOnAction(event -> {
            AuctionItem selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                // No item is selected, show an error message and return
                // ...
                return;
            }
            String bidAmountString = bidAmountField.getText();
            double bidAmount;
            try {
                bidAmount = Double.parseDouble(bidAmountString);
                client.sendBid(bidAmountString, selectedItem.auctionItemId);
            } catch (NumberFormatException e) {
                // Invalid bid amount, show an error message and return
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid bid amount. Please enter a valid number.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            if (bidAmount <= Double.parseDouble(selectedItem.getHighestBid()) || bidAmount < Double.parseDouble(selectedItem.getStartPrice()) * .01) {
                // Invalid bid amount, show an error message and return
                Alert alert = new Alert(Alert.AlertType.ERROR, "Bid amount must be higher than current highest bid and at least 1% higher than start price.", ButtonType.OK);
                alert.showAndWait();
                return;
            }

            // Update the highest bid of the selected item
            selectedItem.setHighestBid(String.valueOf(bidAmount));

            // Update the TableView to display the new highest bid
            tableView.refresh();

            // Update the server with the new bid
            updateServer(String.valueOf(selectedItem.getAuctionItemId()), String.valueOf(bidAmount));
        });

        // Add a list of available auction items
        auctionItemsList = new ListView<>();
        mainLayout.getChildren().add(auctionItemsList);

        // Set up the scene and show the auction window
        Scene scene = new Scene(mainLayout, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Connect to the server
        //client.connectToServer("localhost", 8888);

        // Show the login window and wait for the user to log in
        //LoginWindow loginWindow = new LoginWindow();
        //loginWindow.show();

        // Once the user has logged in, enable the bid button
 
    }
    private void updateServer(String itemId, String bidAmount) {
        // Send a message to the server to update the bid amount for the specified item ID
        boolean success = client.updateBid(itemId, bidAmount);
        client.updateBid(itemId, bidAmount);
        
        if (!success) {
            // An error occurred while communicating with the server, show an error message
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update bid amount. Please try again.", ButtonType.OK);
            alert.showAndWait();
        }
    }
    


    public static void main(String[] args) {
        launch(args);
    }
}

   
