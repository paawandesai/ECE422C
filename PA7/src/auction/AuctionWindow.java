/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* <Paawan Desai>
* <pkd397>
* <17140>
* Spring 2023
* Slip Day Used: 1
*/
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AuctionWindow extends Application {
    private TextField bidAmountField;
    private ListView<AuctionItem> auctionItemsList;
    LoginWindow loginWindow;
    private Timer auctionTimer;
    private int auctionTimeLeft;
    private JLabel timerLabel;
    private Button bidButton;
    private int highestBid;
    Stage primaryStage;
    public static AuctionWindow auctionWindow;
    Client client;
	private BufferedReader fromClient;
	List<AuctionItem> auctionItems;



    
    TableView<AuctionItem> tableView = new TableView<>();
    public AuctionWindow() {
    	auctionWindow = this;
    }


    public synchronized void processInput(String input) {    	
		Gson gson = new Gson();
		Message message = gson.fromJson(input, Message.class);
		System.out.println("[aw] decoded message from server: "+message);

		if(message.messageType == MessageType.SEND_AUCTION_ITEMS) {
			Type listType = new TypeToken<ArrayList<AuctionItem>>(){}.getType();
			this.auctionItems = gson.fromJson(message.jsonAuctionItems, listType);
			System.out.println("[aw] list of auction items from client for SEND_AUCTION_ITEMS "+this.auctionItems);
			Platform.runLater(() -> {
		        tableView.getItems().setAll(this.auctionItems);
		    });		
		}
		else if(message.messageType == MessageType.UPDATE_AUCTION_BID) {
			Type listType = new TypeToken<ArrayList<AuctionItem>>(){}.getType();
			this.auctionItems = gson.fromJson(message.jsonAuctionItems, listType);	
			System.out.println("[aw] list of auction items from client for UPDATE_AUCTION_BID: "+ this.auctionItems);
			Platform.runLater(() -> {
		        tableView.getItems().setAll(this.auctionItems);
		    });	
		}
		
    }
    
    @Override
    public void start(Stage primaryStage) throws UnknownHostException, IOException {
    	Socket socket = new Socket("127.0.0.1", 4281);
    	fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	System.out.println("[aw] Connecting to client " + socket);
				
		Thread readerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("[aw][readerThread] inside run()");
				String input;
				try {
					while ((input = fromClient.readLine()) != null) {
						System.out.println("[aw] From server: " + input);
						processInput(input);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				System.out.println("[client][readerThread] done with run()");					
			}
		});
		Thread refreshThread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("[aw][refreshThread] inside run()");
				while(true) {
					tableView.refresh();
				}
			}
		});
		readerThread.start();
		refreshThread.start();
    	this.primaryStage = primaryStage;
    	Message getAuctionItemList = new Message(MessageType.GET_AUCTION_ITEMS);
    	Client.sendToServer(getAuctionItemList);
//    	while(this.auctionItems == null) {
//    		System.out.println("[aw] syncing fn");
//		}; // bad attempt at syncing
    	System.out.println("[aw] we have auction items in the right place: "+ this.auctionItems);

        // Create the main layout for the auction window
       
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
        if(auctionItems != null) {
        	tableView.getItems().addAll(auctionItems);
        }
       
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
                else if(bidAmount >= Double.parseDouble(selectedItem.getStartPrice())*10){
                	Alert alert3 = new Alert(Alert.AlertType.INFORMATION, "Buy it now price! This item has been sold", ButtonType.OK);
                	alert3.showAndWait();
                }
                
            } catch (NumberFormatException e) {
                // Invalid bid amount, show an error message and return
            	 Alert alert1 = new Alert(AlertType.ERROR);
            	    alert1.setTitle("Invalid Bid Amount");
            	    alert1.setHeaderText(null);
            	    alert1.setContentText("Please enter a valid bid amount as a number.");
            	    alert1.showAndWait();
            }

//          update the auctionItem list with the new bid - done
            selectedItem.setHighestBid(bidAmountString);
            System.out.println("[aw] selected item: " + selectedItem);
            for(int i=0;i<auctionItems.size();i++) {
            	if(auctionItems.get(i).auctionItemId == selectedItem.auctionItemId) {
            		auctionItems.set(i, selectedItem);
            	}
            }
            Message message = new Message(MessageType.UPDATE_AUCTION_BID, this.auctionItems);
            System.out.println("[aw] sending list of auction items with updated bid: " + message.auctionItemsList);
            Client.sendToServer(message);
            tableView.refresh();
//            TODO PAAWAN
//            1 update the auctionItem list with the new bid - done
//            2 send a Message with the updated AuctioItem list, type UPDATE_AUCTION_BID using Client.sendToServer
//            ?? put refresh in a background loop happening every 5 seconds?
            

            
            //send only specific item changed - done
            //update Clients local list - done
            // send that specific item to central list
            // send via server to all clients the updated list
            //send currentBidder to the client if the currentBidder is the highestBidder(defined on Server) compared to main list maintained on Server
            
            
        });

        // Add a list of available auction items
        auctionItemsList = new ListView<>();
        mainLayout.getChildren().add(auctionItemsList);

        // Set up the scene and show the auction window
        Scene scene = new Scene(mainLayout, 600, 600);
        primaryStage.setScene(scene);
        loginWindow = new LoginWindow(this);
        
    }
   
    public TableView<AuctionItem> getTableView() {
        return tableView;
    }
    public void showApplication() {
    	primaryStage.show();
    }



    public static void main(String[] args) {

        launch(args);
    }
}

   