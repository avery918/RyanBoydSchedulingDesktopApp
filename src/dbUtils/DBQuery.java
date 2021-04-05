package dbUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.security.PublicKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DBQuery {


    /** This method returns all customers in the database.
     * @return  Returns all customers. */
    public static ObservableList<Customers> getAllCustomers(){

        ObservableList<Customers> customers = FXCollections.observableArrayList();

        try{
            String sql = "SELECT * FROM customers;";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
               int customerID = resultSet.getInt("Customer_ID");
               String customerName = resultSet.getString("Customer_Name");
               String address = resultSet.getString("Address");
               String postalCode = resultSet.getString("Postal_Code") ;
               String phone = resultSet.getString("Phone");
               int divisionID = resultSet.getInt("Division_ID");

               Customers newCust = new Customers(customerID,customerName,address,postalCode,phone,divisionID);

               customers.add(newCust);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return  customers;
    }

    /** This method returns all appointments in the database.
     * @return  Returns all appointments. */
    public static ObservableList<Appointments> getAllAppointments(){

        ObservableList<Appointments> appointments = FXCollections.observableArrayList();

        try{
            String sql = "SELECT * FROM appointments;";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                int appointmentID = resultSet.getInt("Appointment_ID");
                String title = resultSet.getString("Title");
                String description = resultSet.getString("Description");
                String location = resultSet.getString("Location") ;
                String type = resultSet.getString("Type");
                Timestamp startDateTime = resultSet.getTimestamp("Start");
                Timestamp endDateTime = resultSet.getTimestamp("End");
                int customerID = resultSet.getInt("Customer_ID");
                int userID = resultSet.getInt("User_ID");
                int contactID = resultSet.getInt("Contact_ID");

                Appointments appointments1 = new Appointments(appointmentID,title, description, location, type, startDateTime.toLocalDateTime(),
                        endDateTime.toLocalDateTime(), customerID, userID, contactID);

                appointments.add(appointments1);


            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return  appointments;
    }

    /** This method returns all contacts in the database.
     * @return  Returns all contacts. */
    public static ObservableList<Contact> getAllContacts(){

        ObservableList<Contact> contacts = FXCollections.observableArrayList();

        try{
            String sql = "SELECT * FROM contacts;";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                int contactID = resultSet.getInt("Contact_ID");
                String contactName = resultSet.getString("Contact_Name");
                String email = resultSet.getString("Email");

                Contact contact1 = new Contact(contactID,contactName,email);
                contacts.add(contact1);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return  contacts;
    }

    /** This method returns all first level divisions in the database.
     * @return  Returns all first level divisions. */
    public static ObservableList<FirstLevelDivision> getAllFirstLevelDivisions(){

        ObservableList<FirstLevelDivision> firstLevelDivisions = FXCollections.observableArrayList();

        try{
            String sql = "SELECT * FROM first_level_divisions;";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                int divisionID = resultSet.getInt("Division_ID");
                String division = resultSet.getString("Division");
                int countryID = resultSet.getInt("Country_ID");

                FirstLevelDivision newDivision = new FirstLevelDivision(divisionID,division,countryID);
                firstLevelDivisions.add(newDivision);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return  firstLevelDivisions;
    }

    /** This method returns all countries in the database.
     * @return  Returns all countries*/
    public static ObservableList<Countries> getAllCountries(){

        ObservableList<Countries> countries = FXCollections.observableArrayList();
        try{
            String sql = "SELECT * FROM countries;";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                int countyID = resultSet.getInt("Country_ID");
                String country = resultSet.getString("Country");


                Countries country1 = new Countries(countyID,country);

                countries.add(country1);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  countries;
    }


    /** This method returns all users in the database.
     * @return  Returns all users */
    public static ObservableList<User> getAllUsers(){

        ObservableList<User> users = FXCollections.observableArrayList();

        try{
            String sql = "SELECT * FROM users;";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                int userID = resultSet.getInt("User_ID");
                String userName = resultSet.getString("User_Name");
                String password = resultSet.getString("Password");

                User addUser = new User(userID,userName,password);
                users.add(addUser);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return  users;
    }

    /** This method check to see if the appointment trying to be scheduled overlaps time with an existing appointment.
     * @param start the start date and time of the appointment to be added/modified.
     * @param end the end date and time of the appointment to be added/modified.
     * @param customerId customer id used to check if there will be an overlap for them or not.
     * @param appointId used when a modification to an appointment needs to happen.
     * @return  Returns a string that either empty indicating no overlap was found or a string with indicating
     * an overlap was found. */
    public static  String overLapCheck(LocalDateTime start, LocalDateTime end, int customerId, int appointId){
        String flags = "";
        Timestamp appStartTime = Timestamp.valueOf(start);
        Timestamp appEndTime = Timestamp.valueOf(end);
        String startSqlStmt = "SELECT * FROM appointments where Customer_ID = '" +customerId + "' AND Appointment_ID != '"
                + appointId + "' AND '" + appStartTime + "' >= Start AND '"+ appStartTime +"' <= End";

        String endSqlStmt = "SELECT * FROM appointments where Customer_ID = '" +customerId + "' AND Appointment_ID != '"
                + appointId + "' AND '" + appEndTime + "' >= Start AND '"+ appEndTime +"' <= End";

        String inBetween = "SELECT * FROM appointments WHERE Customer_ID = '" + customerId + "' AND Appointment_ID != '" +appointId+
                "' AND (Start >= '" + appStartTime + "' AND  Start <= '" +appEndTime +
                "' OR End >= '" + appStartTime + "' AND End <= '" +Timestamp.valueOf(end) +"');";



        try {
            PreparedStatement startStmt = DBConnection.getConnection().prepareStatement(startSqlStmt);
            PreparedStatement endStmt = DBConnection.getConnection().prepareStatement(endSqlStmt);
            PreparedStatement inBetweebStmt = DBConnection.getConnection().prepareStatement(inBetween);


            ResultSet startResultSet = startStmt.executeQuery();
            ResultSet endResultSet = endStmt.executeQuery();
            ResultSet inBetweenRS = inBetweebStmt.executeQuery();

            if(startResultSet.next()) {
                String startTime = Appointments.timeConverter(startResultSet.getTimestamp("Start").toLocalDateTime().toLocalTime());
                String endTime =  Appointments.timeConverter(startResultSet.getTimestamp("End").toLocalDateTime().toLocalTime());
                flags += "Start time conflicts with an appointment from " + startTime + " to " + endTime + " that day. Choose a different day or time.\n";
            }

            else if( endResultSet.next()){
                String startTime = Appointments.timeConverter(endResultSet.getTimestamp("Start").toLocalDateTime().toLocalTime());
                String endTime =  Appointments.timeConverter(endResultSet.getTimestamp("End").toLocalDateTime().toLocalTime());
                flags += "End time conflicts with an appointment from " + startTime + " to " + endTime + " that day. Choose a different day or time.\n";
            }

            else if (inBetweenRS.next())
                flags += "Selected times overlap with an appointment scheduled for that day. Choose a different day or time.\n";
        }
        catch(SQLException e){
            e.printStackTrace();
        }
     return  flags;
    }

}
