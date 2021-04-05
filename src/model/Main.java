package model;

import dbUtils.DBConnection;
//import dbUtils.DBDetails;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;


public class Main extends Application {

    /** Method switches to login screen.*/
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/login.fxml"));
        primaryStage.setTitle("Scheduling Application");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    /** Main method.
     * Method open and closes connection to the database*/
    public static void main(String[] args) throws SQLException {
        Connection conn = DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
    }
}
