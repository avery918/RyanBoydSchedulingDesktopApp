package controller;

import dbUtils.DBConnection;
import dbUtils.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointments;
import model.Customers;
import model.FirstLevelDivision;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainMenu implements Initializable {
    @FXML
    private TableView<Customers> customerRecordTbl;

    @FXML
    private TableColumn<Customers, Integer> customerIdCol;

    @FXML
    private TableColumn<Customers, String> customerNameCol;

    @FXML
    private TableColumn<Customers, String> cusAddressCol;

    @FXML
    private TableColumn<Customers, String> postalCodeCol;

    @FXML
    private TableColumn<Customers, String> cusPhoneNumbCol;

    @FXML
    private TableColumn<Customers, String> cusRegionCol;

    @FXML
    private RadioButton allAppRdl;

    @FXML
    private RadioButton byMonthRdl;

    @FXML
    private RadioButton byWeekRdl;

    @FXML
    private TableView<Appointments> appointmentTbl;

    @FXML
    private TableColumn<Appointments, Integer> appointIdCol;

    @FXML
    private TableColumn<Appointments, String> appointTitleCol;

    @FXML
    private TableColumn<Appointments, String> appointDescriptCol;

    @FXML
    private TableColumn<Appointments, String> appointLocCol;

    @FXML
    private TableColumn<Appointments, String> appContactNameCol;

    @FXML
    private TableColumn<Appointments, String> appointTypeCol;

    @FXML
    private TableColumn<Appointments, String> AppointStartCol;

    @FXML
    private TableColumn<Appointments, String> appointEndCol;

    @FXML
    private TableColumn<Appointments, Integer> appCustomerIdCol;

    private Stage stage;
    private Parent scene;


    /** This method initializes the main menu.
     * It prepopulate the customer table and appointment table with any existing data.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        /** Gets customers in the database and populates the customer table view. */
        customerRecordTbl.setItems(DBQuery.getAllCustomers());

        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        cusAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        cusPhoneNumbCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        cusRegionCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));



        appointmentTbl.setItems(DBQuery.getAllAppointments());

        appointIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointDescriptCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointLocCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        appContactNameCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        appointTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        AppointStartCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        appointEndCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        appCustomerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }

    /** This event handler switches to the add appointment screen.
     *  @param  event action that triggers method*/
    @FXML
    void addAppointment(ActionEvent event) throws IOException {
        screenSwitch(event, "/view/addAppointment.fxml");
    }

    /** This event handler switches to the add customer screen.
     *  @param  event action that triggers method*/
    @FXML
    void addNewCustomer(ActionEvent event) throws IOException {
        screenSwitch(event, "/view/addCustomer.fxml");
    }

    /** This event handler deletes an appointment from the database.
     * After the appointment is deleted the appointment table is repopulated with current availiable appointments.
     *  @param  event action that triggers method*/
    @FXML
    void deleteAppointment(ActionEvent event) {

        try {
            int appointID = appointmentTbl.getSelectionModel().getSelectedItem().getAppointmentId();
            String type = appointmentTbl.getSelectionModel().getSelectedItem().getType();

            String deleteAppoint = "Delete from appointments where Appointment_ID = " + String.valueOf(appointID) + ";";


            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Appointment ID " + appointID + " - " + type + " will be deleted. Are you sure?");
            Optional<ButtonType> toDelete = alert.showAndWait();

            if(toDelete.get() == ButtonType.OK) {
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(deleteAppoint);
                ps.execute();
                appointmentTbl.setItems(DBQuery.getAllAppointments());

            }
            else {
                Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION,"Appointment was not deleted!");
                Optional<ButtonType> yesDelete = alert2.showAndWait();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (NullPointerException e) {
            Alert correctInput = new Alert(Alert.AlertType.ERROR);
            correctInput.setTitle("No Appointment Chosen!");
            correctInput.setContentText("Please  choose a appointment to delete.");
            correctInput.showAndWait();
        }
    }


    /** This event handler deletes a customer from the database.
     * Due to foreign key restraints, it first deletes all appointments associated with that customer.
     * After that the customer is deleted.
     * After the customer and their appointments is deleted the customer and appointment tables are repopulated with current availiable data.
     *  @param  event action that triggers method*/
    @FXML
    void deleteCustomer(ActionEvent event) {

            try {
                int custID = customerRecordTbl.getSelectionModel().getSelectedItem().getCustomerID();

                String deleteCusApp = "DELETE  appointments FROM customers inner join appointments" +
                        " on customers.Customer_ID = appointments.Customer_ID where customers.Customer_ID = " +
                        String.valueOf(custID) ;
                String deleteCust = "Delete from customers where Customer_ID = " + String.valueOf(custID) + ";";


                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Deleting customer will delete appointments associated with this customer. Still proceed?");
                Optional<ButtonType> toDelete = alert.showAndWait();

                if(toDelete.get() == ButtonType.OK) {
                    PreparedStatement ps = DBConnection.getConnection().prepareStatement(deleteCusApp);
                    PreparedStatement ps1 = DBConnection.getConnection().prepareStatement(deleteCust);
                    ps.execute();
                    ps1.execute();
                    customerRecordTbl.setItems(DBQuery.getAllCustomers());
                    appointmentTbl.setItems(DBQuery.getAllAppointments());

                }
                else {
                    Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION,"Customer was not deleted!");
                    Optional<ButtonType> yesDelete = alert2.showAndWait();
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (NullPointerException e) {
                Alert correctInput = new Alert(Alert.AlertType.ERROR);
                correctInput.setTitle("Customer to Delete");
                correctInput.setContentText("Please  choose a customer to delete.");
                correctInput.showAndWait();
            }
    }

    /** This event handler switches to the reports screen.
     *  @param  event action that triggers method*/
    @FXML
    void getReport(ActionEvent event) throws IOException {
        screenSwitch(event, "/view/report.fxml");

    }

    /** This event handler switches to the modify appointment screen.
     * After an appointment is selected its data is sent over to prepopulate the modify appointment screen.
     *  @param  event action that triggers method*/
    @FXML
    void modifyAppointment(ActionEvent event) throws IOException {
        if (appointmentTbl.getSelectionModel().getSelectedItem() == null){
            Alert correctInput = new Alert(Alert.AlertType.ERROR);
            correctInput.setTitle("Choose Appointment");
            correctInput.setContentText("Please make sure to choose an appointment to modify.");
            correctInput.showAndWait();
        }
        else{
            ModifyAppointment.setAppointments(appointmentTbl.getSelectionModel().getSelectedItem());
            screenSwitch(event, "/view/modifyAppointment.fxml");
        }
    }

    /** This event handler switches to the modify customer screen.
     * After a customer is selected its data is sent over to prepopulate the modify customer screen.
     *  @param  event action that triggers method*/
    @FXML
    void modifyCustomer(ActionEvent event) throws IOException {
        if (customerRecordTbl.getSelectionModel().getSelectedItem() == null){
            Alert correctInput = new Alert(Alert.AlertType.ERROR);
            correctInput.setTitle("Choose Customer");
            correctInput.setContentText("Please make sure to choose a customer to modify.");
            correctInput.showAndWait();
        }
        else{
            ModifyCustomer.initCustomer(customerRecordTbl.getSelectionModel().getSelectedItem());
            screenSwitch(event, "/view/modifyCustomer.fxml");
        }

    }



    /** This event handler logs the user out of the application and switches back to the login screen.
     *  @param  event action that triggers method*/
    @FXML
    void signOut(ActionEvent event) throws IOException {
        Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION,"Sign out?");
        Optional<ButtonType> signOut = alert2.showAndWait();

        if(signOut.get() == ButtonType.OK)
            screenSwitch(event, "/view/login.fxml");
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

    /** This event handler displays all appointments in the system in the appointment table.
     @param event action that triggers method.
     */
    @FXML
    void showAll(ActionEvent event) {
        appointmentTbl.setItems(DBQuery.getAllAppointments());

    }


    /** This event handler displays appointments for the current month in the appointment table.
     @param event action that triggers method.
     */
    @FXML
    void showByMonth(ActionEvent event) {
        String   sql = "SELECT * FROM appointments where date_format(sysdate(), " +
                "'%Y-%m-01') = date_format(Start, '%Y-%m-01');";
        showAppointmentsBy(sql);
    }

    /** This event handler displays appointments for the current week in the appointment table.
     @param event action that triggers method.
     */
    @FXML
    void showByWeek(ActionEvent event) {
        String sql = "SELECT * FROM WJ07su1.appointments where date_format(sysdate(), '%v') = date_format(Start, '%v');";
        showAppointmentsBy(sql);
    }


    /** This method takes in a string initialize with a sql statement and based of the statement
     * sets the appointment table to display the requested appointments.
     @param sqlStmt sql statement used to determine how to display the appointments.
     */
    public void showAppointmentsBy(String sqlStmt){


        ObservableList<Appointments> appointments = FXCollections.observableArrayList();
        try{
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sqlStmt);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                int appointmentID = resultSet.getInt("Appointment_ID");
                String title = resultSet.getString("Title");
                String description = resultSet.getString("Description");
                String location = resultSet.getString("Location") ;
                String type = resultSet.getString("Type");
                LocalDate startDate = resultSet.getDate("Start").toLocalDate();
                LocalTime startTime = resultSet.getTime("Start").toLocalTime();
                LocalDate endDate = resultSet.getDate("End").toLocalDate();
                LocalTime endTime = resultSet.getTime("End").toLocalTime();
                int customerID = resultSet.getInt("Customer_ID");
                int userID = resultSet.getInt("User_ID");
                int contactID = resultSet.getInt("Contact_ID");

                Appointments appointments1 = new Appointments(appointmentID,title, description, location, type, LocalDateTime.of(startDate, startTime), LocalDateTime.of(endDate,endTime),
                        customerID, userID, contactID);

                appointments.add(appointments1);
            }
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        appointmentTbl.setItems(appointments);
    }

}
