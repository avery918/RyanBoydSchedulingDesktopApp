package controller;

import dbUtils.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Appointments;
import model.ReportInterface;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;



public class Report implements Initializable {

    private Stage stage;
    private Parent scene;

    @FXML
    private TextArea reportTxt;


    /**Lambda Expression.*/
    ReportInterface companyReport = () -> {
        String report = monthlyAppReport() + contactScheduling() + customerRetention();
        return report;
    };

    /** This method calls the lambda expression and takes the information from it and displays it in a textfield.
     * Generates three reports: monthly appointment report, the contact scheduling, and customer retention report.

     ** discussion of lambda
     * This lambda expression calling the methods that generate the separate reports and concatenating them together.
     * This lambda expression was used to prevent having to form multiple new StringBuilder objects.
     * return Returns a string containing the three reports.
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String myReports = companyReport.getReport();

        reportTxt.setText(String.valueOf(myReports));

    }



    /** This event handler returns back to the main menu
     @param event action that triggers method.
     */
    @FXML
    void backToMainMenu(ActionEvent event) throws IOException {
        screenSwitch(event, "/view/mainMenu.fxml");
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

    /** This method takes a sql statement for getting the appointments for each month by type.
     @return Returns a String with all the appointments grouped by month and type
     */
    public String monthlyAppReport() {

        String firstReport = "SELECT monthname(Start) as AppMonth, Type, count(*) as TotalAmount \n" +
                "FROM WJ07su1.appointments \n" +
                "group by  AppMonth, type;";

        String addOn = "";
        addOn += "                    Customer Appointment Report\n"
                + "--------------------------------------------------------------\n";

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(firstReport);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                addOn += "There is(are) " + resultSet.getInt("TotalAmount") +
                        " " + resultSet.getString("type") + " appointment(s) in "
                        + resultSet.getString("AppMonth") + ".\n";
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return addOn;

    }

    /** This method takes a sql statement for getting each contacts and their appointments.
     @return Returns a String with the contacts in the database and all of their associated.
     */
    public  String contactScheduling(){
        String addOn = "";


        addOn += "\n\n\n\n\n                     Contact's Schedule Report\n" +
                "--------------------------------------------------------------\n";

        String contactSched = "SELECT a.Appointment_ID, a.Title, a.Type, a.Description, a.Start, a.End, \n" +
                "a.Customer_ID, c.Contact_Name\n" +
                "FROM WJ07su1.appointments as a, contacts as c\n" +
                "where a.Contact_ID = c.Contact_ID\n" +
                "order by c.Contact_Name;";

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(contactSched);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                addOn += resultSet.getString("Contact_Name") + " appointment information : " +
                        "Appointment ID - " + resultSet.getInt("Appointment_ID") + ", Title - " + resultSet.getString("Title") +
                        ", Type -  " + resultSet.getString("Type") + ", Description - " + resultSet.getString("Description") +
                        ", Start Date/Time " + resultSet.getTimestamp("Start").toLocalDateTime().toLocalDate() + " " +
                        Appointments.timeConverter(resultSet.getTimestamp("Start").toLocalDateTime().toLocalTime())
                        + ", End Date/Time " + resultSet.getTimestamp("End").toLocalDateTime().toLocalDate() + " " +
                        Appointments.timeConverter(resultSet.getTimestamp("End").toLocalDateTime().toLocalTime()) + ", Customer ID -"
                        + resultSet.getInt("a.Customer_ID") + ".\n\n";
            }
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return addOn;

    }

    /** This method takes a sql statement and gets the customers that were added and retain by year.
     @return Returns a String with the number of customers add  and kept by year.
     */
    public String customerRetention(){
        String addOn = "";

        addOn += "\n\n\n\n\n                     Customer Retention Report\n" +
                "--------------------------------------------------------------\n";

        String sqlStmt = "SELECT date_format(Create_Date, '%Y') as createDate, count(*) as totalCustomers FROM WJ07su1.customers " +
                "group by createDate;";

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sqlStmt);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {

                addOn += "Ryan Corporation added and retained " + resultSet.getInt("totalCustomers") + " customers in the year "
                + resultSet.getString("createDate") + ".\n";
            }
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return  addOn;
    }
}
