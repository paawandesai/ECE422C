/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* <Paawan Desai>
* <pkd397>
* <17140>
* Spring 2023
*/
package auction;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginWindow extends Dialog {

    private TextField usernameField;
    private TextField passwordField;
    AuctionWindow auctionWindow;
  
    
    public LoginWindow(AuctionWindow auctionWindow) {
    	this.auctionWindow = auctionWindow;
   
    
        // Create the main layout for the login window
    	setTitle("Login Window");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        // Add labels and fields for the username and password
        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);

        usernameField = new TextField();
        GridPane.setConstraints(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);

        passwordField = new TextField();
        GridPane.setConstraints(passwordField, 1, 1);

        // Add a login button
        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);

        // Add all the elements to the main layout
        grid.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton);

        // Set up the scene and show the login window
        Scene scene = new Scene(grid, 300, 150);
        this.getDialogPane().setContent(grid);
        this.show();
        loginButton.setOnAction(event -> {
            // Get the entered username and password
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Validate the credentials
            if (username.equals("admin") && password.equals("password") || username.equals("guest")) {
                // If the credentials are valid, open the AuctionWindow
            	auctionWindow.showApplication();
                this.close();
            } else {
                // If the credentials are invalid, display an error message
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Invalid Login");
                alert.setHeaderText("Invalid username or password");
                alert.setContentText("Please enter a valid username and password.");
                alert.showAndWait();
            }
        });
    }


}
