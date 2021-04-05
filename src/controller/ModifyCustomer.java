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
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ModifyCustomer implements Initializable {

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

    /** Static variable used to get the customer's data that needs to be modified */
    private static Customers customer = null;

    /** Static method that initializes the static variable with the customer's data
     * @param c customer to be modified*/
    public static void initCustomer(Customers c) {
        customer = c;

    }

    /** This method prepopulate the modifyCustomer screen with the customer to be modified.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerCountryCbox.setItems(DBQuery.getAllCountries());
        firstDivisionCbox.setItems(DBQuery.getAllFirstLevelDivisions());

        customerIdTxt.setText(Integer.toString(customer.getCustomerID()));
        customerNameTxt.setText(customer.getCustomerName());
        customerAddressTxt.setText(customer.getAddress());
        postalCodeTxt.setText(customer.getPostalCode());
        phoneNumberTxt.setText(customer.getPhone());

        int countyId = 0;
        for (int i = 0; i < DBQuery.getAllFirstLevelDivisions().size(); i++) {
            if (DBQuery.getAllFirstLevelDivisions().get(i).getDivisionId() == customer.getDivisionID()) {

                setFirstDivisionCbox(DBQuery.getAllFirstLevelDivisions().get(i).getCountryId());
                firstDivisionCbox.getSelectionModel().select(DBQuery.getAllFirstLevelDivisions().get(i));
                countyId = firstDivisionCbox.getSelectionModel().getSelectedItem().getCountryId();
            }

        }
        for (int i = 0; i < customerCountryCbox.getItems().size(); i++) {
            if (customerCountryCbox.getItems().get(i).getCountryId() == countyId) {
                customerCountryCbox.getSelectionModel().select(i);
                setFirstDivisionCbox(customerCountryCbox.getValue().getCountryId());

            }
        }

    }


    /** This event handler returns back to the main menu without saving anything to the database.
     @param event action that triggers method.
     */
    @FXML
    void cancelToMain(ActionEvent event) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No changes were made. Still cancel?");
        Optional<ButtonType> toDelete = alert.showAndWait();
        if (toDelete.get() == ButtonType.OK)
            screenSwitch(event, "/view/mainMenu.fxml");
    }

    @FXML
    void saveToMain(ActionEvent event) throws IOException {

        String updatCust = " UPDATE customers SET Customer_Name = ?," +
                " Address = ?, Postal_Code = ?, Phone = ?," +
                " Division_ID = ? Where Customer_ID = " + String.valueOf(customer.getCustomerID()) + ";";

        String flags = "";

        try {

            flags = Customers.logicCheck(customerNameTxt.getText(), customerAddressTxt.getText(), postalCodeTxt.getText(), phoneNumberTxt.getText(),
                    firstDivisionCbox.getSelectionModel().getSelectedItem());
            if (flags != "") {
                Alert correctInput = new Alert(Alert.AlertType.ERROR);
                correctInput.setTitle("Error!");
                correctInput.setContentText(flags);
                correctInput.showAndWait();
            } else {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(updatCust);
                ps.setString(1, customerNameTxt.getText());
                ps.setString(2, customerAddressTxt.getText());
                ps.setString(3, postalCodeTxt.getText());
                ps.setString(4, phoneNumberTxt.getText());
                ps.setInt(5, firstDivisionCbox.getSelectionModel().getSelectedItem().getDivisionId());


                ps.execute();
                screenSwitch(event, "/view/mainMenu.fxml");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            Alert correctInput = new Alert(Alert.AlertType.ERROR);
            correctInput.setTitle("Region Not Selected");
            correctInput.setContentText("Please make sure to choose a region.");
            correctInput.showAndWait();
        }

    }

    /** This event handler associates a country with its first level divisions.
     * resets firstDivisionCbox when a new country is chosen.
     @param event action that triggers method.

     */
    @FXML
    void selectCountry(ActionEvent event) {
        setFirstDivisionCbox(customerCountryCbox.getValue().getCountryId());
        firstDivisionCbox.getSelectionModel().select(0);
    }

    /** This method filters the first level divisions based on the country selected.
     * Sets firstDivisionCbox to the filter ObservableList of first level divisions.
     @param countryId id of country needed to filter first level divisions.
     */
    public void setFirstDivisionCbox(int countryId) {

        ObservableList<FirstLevelDivision> firstLevelDivisions = FXCollections.observableArrayList();

        for (FirstLevelDivision fld : DBQuery.getAllFirstLevelDivisions()) {

            if (fld.getCountryId() == countryId)
                firstLevelDivisions.add(fld);
        }
        firstDivisionCbox.setItems(firstLevelDivisions);
    }


    /** This method switches to desired screen when called
     @param event action that triggers method.
     @param screen desired screen to switch to.
     */
    public void screenSwitch(ActionEvent event, String screen) throws IOException {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(screen));
        stage.setScene(new Scene(scene));
        stage.show();
    }

}
