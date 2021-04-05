package controller;

import dbUtils.DBConnection;
import dbUtils.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Countries;
import model.Customers;
import model.FirstLevelDivision;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddCustomer implements Initializable {
    @FXML
    private TextField customerIdTxt;

    @FXML
    private TextField customerNameTxt;

    @FXML
    private TextField customerAddressTxt;

    @FXML
    private TextField postalCodeTxt;

    @FXML
    private TextField phoneNumberTxt;

    @FXML
    private ComboBox<Countries> customerCountryCbox;

    @FXML
    private ComboBox<FirstLevelDivision> firstDivisionCbox;

    private Stage stage;
    private Parent scene;



    /** This method initializes the country and first level division combo boxes.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerCountryCbox.setItems(DBQuery.getAllCountries());
        firstDivisionCbox.setItems(DBQuery.getAllFirstLevelDivisions());
    }


    /** This event handler returns back to the main menu without saving anything to the database.
     @param event action that triggers method.
     */
    @FXML
    void cancelToMain(ActionEvent event) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Customer was not saved. Still cancel?");
        Optional<ButtonType> toDelete = alert.showAndWait();
        if (toDelete.get() == ButtonType.OK)
            screenSwitch(event, "/view/mainMenu.fxml");
    }

    /** This event handler saves a new customer to the database and returns back to the main menu.
     * Calls other methods to perform logic checks. Throws alert if error is found.
     @param event action that triggers method.
     */
    @FXML
    void saveToMain(ActionEvent event) throws IOException {

        String addCust = " INSERT INTO customers VALUES(NULL,?,?,?,?,?,NULL,NULL,NULL,?);";
        String flags = "";

        try {

            flags = Customers.logicCheck(customerNameTxt.getText(),customerAddressTxt.getText(),postalCodeTxt.getText(), phoneNumberTxt.getText(),
                    firstDivisionCbox.getSelectionModel().getSelectedItem());
            if (flags != "") {
                Alert correctInput = new Alert(Alert.AlertType.ERROR);
                correctInput.setTitle("Error!");
                correctInput.setContentText(flags);
                correctInput.showAndWait();
            }
            else {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(addCust);
                ps.setString(1, customerNameTxt.getText());
                ps.setString(2 ,customerAddressTxt.getText());
                ps.setString(3,postalCodeTxt.getText());
                ps.setString(4,phoneNumberTxt.getText());
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(6,firstDivisionCbox.getSelectionModel().getSelectedItem().getDivisionId());

                ps.execute();
                screenSwitch(event, "/view/mainMenu.fxml");
            }

        }
        catch (SQLException throwables ) {
            throwables.printStackTrace();
        }
        catch(NullPointerException e){
            Alert correctInput = new Alert(Alert.AlertType.ERROR);
            correctInput.setTitle("Region Not Selected");
            correctInput.setContentText("Please make sure to choose a region.");
            correctInput.showAndWait();

        }


    }
    /** This event handler associates a country with its first level divisions.
     @param event action that triggers method.
     */
    @FXML
    void selectCountry(ActionEvent event) {
        setFirstDivisionCbox(customerCountryCbox.getValue().getCountryId());
    }

    /** This method filters the first level divisions based on the country selected.
     * Sets firstDivisionCbox to the filter ObservableList of first level divisions.
     @param countryId id of country needed to filter first level divisions.
     */
    public void setFirstDivisionCbox(int countryId){

        ObservableList<FirstLevelDivision> firstLevelDivisions = FXCollections.observableArrayList();

        for (FirstLevelDivision fld: DBQuery.getAllFirstLevelDivisions()){
            if (fld.getCountryId() == countryId)
                firstLevelDivisions.add(fld);
        }
        firstDivisionCbox.setItems(firstLevelDivisions);
    }


    /** This method switches to desired screen when called
     @param event action that triggers method.
     @param screen desired screen to switch to.
     */
    public void screenSwitch (ActionEvent event, String screen) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(screen));
        stage.setScene(new Scene(scene));
        stage.show();
    }


}
