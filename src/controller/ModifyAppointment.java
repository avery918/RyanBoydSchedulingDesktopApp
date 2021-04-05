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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class ModifyAppointment implements Initializable {

    @FXML
    private TextField modAddIDTxt;

    @FXML
    private ComboBox<Contact> modContactCbox;

    @FXML
    private DatePicker modStartDateDatepicker;

    @FXML
    private DatePicker modEndDateDatepicker;

    @FXML
    private ComboBox<String> startTimeCbox;

    @FXML
    private ComboBox<String> endTimeCbox;

    @FXML
    private ComboBox<Customers> modCustomerIDCbox;

    @FXML
    private ComboBox<User> modUserCbox;

    @FXML
    private TextField modLocationTxt;

    @FXML
    private TextField titleTxt;

    @FXML
    private TextField descriptionTxt;

    @FXML
    private TextField typeTxt;

    Stage stage;
    Parent scene;

    /** Static variable used to get the appointment's data that needs to be modified */
    private static Appointments appointments = null;

    /** Static method that initializes the static variable with the appointment's data
     * @param a appointment to be modified*/
    public static void setAppointments(Appointments a){
        appointments = a;

    }




    /** This method prepopulate the modifyAppointment screen with the appointment to be modified.
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

        startTimeCbox.setVisibleRowCount(7);
        endTimeCbox.setVisibleRowCount(7);


        typeTxt.setText(appointments.getType());
        descriptionTxt.setText(appointments.getDescription());
        titleTxt.setText(appointments.getTitle());
        modLocationTxt.setText(appointments.getLocation());
        modStartDateDatepicker.setValue(appointments.getStart().toLocalDate());
        startTimeCbox.getSelectionModel().select(Appointments.timeConverter(appointments.getStart().toLocalTime()));
        modEndDateDatepicker.setValue(appointments.getEnd().toLocalDate());
        endTimeCbox.getSelectionModel().select(Appointments.timeConverter(appointments.getEnd().toLocalTime()));
        modAddIDTxt.setText(String.valueOf(appointments.getAppointmentId()));

       for (Customers c : DBQuery.getAllCustomers()){
           if (c.getCustomerID() == appointments.getCustomerID()){
               modCustomerIDCbox.setItems(DBQuery.getAllCustomers());
               modCustomerIDCbox.getSelectionModel().select(c);
           }
       }
        for (Contact c : DBQuery.getAllContacts()){
            if (c.getContactId() == appointments.getContactID()){
                modContactCbox.setItems(DBQuery.getAllContacts());
                modContactCbox.getSelectionModel().select(c);
            }
        }
        for (User u : DBQuery.getAllUsers()){
            if (u.getUserID() == appointments.getUserID()){
                modUserCbox.setItems(DBQuery.getAllUsers());
                modUserCbox.getSelectionModel().select(u);
            }
        }

    }




    /** This event handler returns back to the main menu without saving anything to the database.
     @param event action that triggers method.
     */
    @FXML
    void CancelToMain(ActionEvent event) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"No changes were made. Still cancel?");
        Optional<ButtonType> toDelete = alert.showAndWait();
        if (toDelete.get() == ButtonType.OK)
            screenSwitch(event, "/view/mainMenu.fxml");
    }


    /** This event handler updates the appointment in database and returns back to the main menu.
     * Calls other methods to perform logic checks. Throws alert if error is found.
     @param event action that triggers method.
     */
    @FXML
    void SaveToMain(ActionEvent event)  throws IOException{
        String updateApp = " UPDATE appointments SET Title = ?," +
                " Description = ?, Location = ?, Type = ?," +
                " Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ?" +
                " Where Appointment_ID = " + String.valueOf(appointments.getAppointmentId()) + ";" ;
        String flag = "";
        String flag2 = "";


        try {
            Appointments.reverseConverter(startTimeCbox.getSelectionModel().getSelectedItem());

            String title = titleTxt.getText();
            String description = descriptionTxt.getText();
            String location = modLocationTxt.getText();
            int contactId = modContactCbox.getSelectionModel().getSelectedItem().getContactId();
            String type = typeTxt.getText();
            String startTime = Appointments.reverseConverter(startTimeCbox.getSelectionModel().getSelectedItem());
            String endTime =   Appointments.reverseConverter(endTimeCbox.getSelectionModel().getSelectedItem());
            int customerId = modCustomerIDCbox.getSelectionModel().getSelectedItem().getCustomerID();
            int userId = modUserCbox.getSelectionModel().getSelectedItem().getUserID();


            LocalDateTime startDate = LocalDateTime.of(modStartDateDatepicker.getValue(),
                    LocalTime.parse(startTime));
            LocalDateTime endDate = LocalDateTime.of(modEndDateDatepicker.getValue(),
                    LocalTime.parse(endTime));


            flag = Appointments.logicCheck(startDate, endDate, customerId, appointments.getAppointmentId());
            flag2 = emptyFields(title, description, location, type);

            if (flag != "" || flag2 != ""){
                String errors = flag + flag2;
                Alert correctInput = new Alert(Alert.AlertType.ERROR);
                correctInput.setTitle("Appointment Date/Time Error.");
                correctInput.setContentText(errors);
                correctInput.showAndWait();
            }
            else {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(updateApp);

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

    public String emptyFields(String title, String description, String location, String type){

        String flag = "";

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || type.isEmpty())
            flag += "Please fill in all empty fields.";

        return flag;
    }
}
