package controller;

import dbUtils.DBConnection;
import dbUtils.DBQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class AddAppointment implements Initializable {

    @FXML
    private TextField appointmentIdTxt;

    @FXML
    private ComboBox<String> startTimeCbox;

    @FXML
    private ComboBox<String> endTimeCbox;

    @FXML
    private ComboBox<Contact> contactCbox;

    @FXML
    private DatePicker startDateDatePicker;

    @FXML
    private DatePicker endDateDatePicker;

    @FXML
    private ComboBox<Customers> customerIdCbox;

    @FXML
    private ComboBox<User> userCbox;

    @FXML
    private TextField locationTxt;

    @FXML
    private TextField titleTxt;

    @FXML
    private TextField descriptionTxt;

    @FXML
    private TextField typeTxt;

    private Stage stage;
    private Parent scene;



    /** This method initializes the startTime, endTime, customer, user, and contact combo boxes.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LocalTime startTime = LocalTime.of(0,0);
        LocalTime endTime =LocalTime.of(23,0);

        while(startTime.isBefore(endTime.plusSeconds(1))) {
            startTimeCbox.getItems().add(Appointments.timeConverter(startTime));

            endTimeCbox.getItems().add(Appointments.timeConverter(startTime));


            startTime = startTime.plusMinutes(15);
        }

        startTimeCbox.setVisibleRowCount(5);
        endTimeCbox.setVisibleRowCount(5);

        customerIdCbox.setItems(DBQuery.getAllCustomers());
        contactCbox.setItems(DBQuery.getAllContacts());
        userCbox.setItems(DBQuery.getAllUsers());


    }

    /** This event handler returns back to the main menu without saving anything to the database.
     @param event action that triggers method.
     */
    @FXML
    void CancelToMain(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"No appointment was added. Back to main menu?");
        Optional<ButtonType> toDelete = alert.showAndWait();
        if (toDelete.get() == ButtonType.OK)
            screenSwitch(event, "/view/mainMenu.fxml");
    }

    /** This event handler saves a new appointment to the database and returns back to the main menu.
     * Calls other methods to perform logic checks. Throws alert if error is found.
     @param event action that triggers method.
     */
    @FXML
    void SaveToMain(ActionEvent event) throws IOException {
        String addAppointment = " INSERT INTO appointments VALUES(NULL,?,?,?,?,?,?,NULL,NULL,NULL,NULL,?,?,?);";
        String flag = "";

        try {
//            Appointments.reverseConverter(startTimeCbox.getSelectionModel().getSelectedItem());

            String title = titleTxt.getText();
            String description = descriptionTxt.getText();
            String location = locationTxt.getText();
            int contactId = contactCbox.getSelectionModel().getSelectedItem().getContactId();
            String type = typeTxt.getText();
            String startTime = Appointments.reverseConverter(startTimeCbox.getSelectionModel().getSelectedItem());
            String endTime =   Appointments.reverseConverter(endTimeCbox.getSelectionModel().getSelectedItem());


            LocalDateTime startDate = LocalDateTime.of(startDateDatePicker.getValue(),
                    LocalTime.parse(startTime));
            LocalDateTime endDate = LocalDateTime.of(endDateDatePicker.getValue(),
                    LocalTime.parse(endTime));


            int customerId = customerIdCbox.getSelectionModel().getSelectedItem().getCustomerID();
            int userId = userCbox.getSelectionModel().getSelectedItem().getUserID();


            flag = Appointments.logicCheck(startDate, endDate, customerId, -999);

            if (flag != ""){
                Alert correctInput = new Alert(Alert.AlertType.ERROR);
                correctInput.setTitle("Appointment Date/Time Error.");
                correctInput.setContentText(flag);
                correctInput.showAndWait();
            }
            else {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(addAppointment);

                ps.setString(1, title);
                ps.setString(2, description);
                ps.setString(3, location);
                ps.setString(4, type);
                ps.setTimestamp(5, Timestamp.valueOf(startDate));
                ps.setTimestamp(6, Timestamp.valueOf(endDate));
                ps.setInt(7, customerId);
                ps.setInt(8, userId);
                ps.setInt(9, contactId);

                ps.execute();
                screenSwitch(event, "/view/mainMenu.fxml");
            }
        }


        catch (SQLException throwables ) {
            throwables.printStackTrace();
        }
        catch(NullPointerException e) {
            Alert correctInput = new Alert(Alert.AlertType.ERROR);
            correctInput.setTitle("Missing Entry");
            correctInput.setContentText("Please make sure to fill out every field.");
            correctInput.showAndWait();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

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
