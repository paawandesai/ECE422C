package auction;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JLabel;

public class AuctionWindow extends Application {
    private TextField bidAmountField;
    private ListView<AuctionItem> auctionItemsList;
    private Timer auctionTimer;
    private int auctionTimeLeft;
    private JLabel timerLabel;
    private Button bidButton;
    private int highestBid;
    
    TableView<AuctionItem> tableView = new TableView<>();


    @Override
    public void start(Stage primaryStage) {
        // Create the main layout for the auction window
    	// Get the list of auction items from the server
       
        List<AuctionItem> auctionItems = server.readItemsFromFile();
        // Start listening for updates from the server
        primaryStage.setTitle("Auction Window");

        VBox mainLayout = new VBox();
        mainLayout.setPadding(new Insets(10, 10, 10, 10));
        mainLayout.setSpacing(10);
        
        //Item ID
        TableColumn<AuctionItem, Integer> idColumn = new TableColumn<>("Item ID");
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
                    	setText(String.format("$%.2f", Double.parseDouble(currentBid)));
                    }
                }
            };

            // Set a listener on the item property of the cell to update the current bid when it changes
            cell.itemProperty().addListener((obs, oldBid, newBid) -> {
                if (newBid != null) {
                	cell.setText(String.format("$%.2f", Double.parseDouble(newBid)));
                } else {
                    cell.setText(null);
                }
            });

            return cell;
        });
        tableView.getColumns().addAll(idColumn, nameColumn, descriptionColumn, priceColumn, currentBidColumn);
        tableView.getItems().addAll(auctionItems);
        mainLayout.getChildren().add(tableView);
      
     
        
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
            	Alert alert = new Alert(AlertType.ERROR);
        	    alert.setTitle("No Item Selected");
        	    alert.setHeaderText(null);
        	    alert.setContentText("Please select an item from the above table");
        	    alert.showAndWait();
                return;
            }
            String bidAmountString = bidAmountField.getText();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, bidAmountString);
            alert.show();
            try {
                double bidAmount = Double.parseDouble(bidAmountString);
                if(bidAmountString.isEmpty()) {
                	Alert alert3 = new Alert(Alert.AlertType.ERROR, "Enter a Bid Amount that is higher than start bid", ButtonType.OK);
                	alert3.showAndWait();
                }
                else if (bidAmount <= Double.parseDouble(selectedItem.getHighestBid()) || bidAmount < (Double.parseDouble(selectedItem.getStartPrice()) * 1.01)) {
                    // Invalid bid amount, show an error message and return
                    Alert alert2 = new Alert(Alert.AlertType.ERROR, "Bid amount must be higher than current highest bid and at least 1% higher than start price.", ButtonType.OK);
                    alert2.showAndWait();
                }
                
                //client.sendBid(bidAmountString, selectedItem.auctionItemId);
            } catch (NumberFormatException e) {
                // Invalid bid amount, show an error message and return
            	 Alert alert1 = new Alert(AlertType.ERROR);
            	    alert1.setTitle("Invalid Bid Amount");
            	    alert1.setHeaderText(null);
            	    alert1.setContentText("Please enter a valid bid amount as a number.");
            	    alert1.showAndWait();
            }

            // Update the highest bid of the selected item
            selectedItem.setHighestBid(bidAmountString);

            // Update the TableView to display the new highest bid
            tableView.refresh();

            // Update the server with the new bid
            Message message = new Message(Message.updateAuctionItems, "");
            System.out.println(message);
            // client.sendToServer(message);
            tableView.refresh();
            
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
    public void updateAuctionItem(AuctionItem item) {
        Platform.runLater(() -> {
        	ObservableList<AuctionItem> items = auctionItemsList.getItems();
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getAuctionItemId().equals(item.getAuctionItemId())) {
                	items.set(i, item);
                    tableView.refresh();
                    break;
                }
            }
        });
    }
    public TableView<AuctionItem> getTableView() {
        return tableView;
    }



    public static void main(String[] args) {
        launch(args);
    }
}

   
