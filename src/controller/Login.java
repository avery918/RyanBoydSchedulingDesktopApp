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
import model.Appointments;
import model.LocalTimeZoneInterface;

import java.io.IOException;
import java.io.*;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Login implements Initializable {


    @FXML
    private Label userLoginLbl;

    @FXML
    private Label usernameLbl;

    @FXML
    private TextField userNameTxt;

    @FXML
    private Label passwordLbl;

    @FXML
    private PasswordField passwordTxt;

    @FXML
    private Button loginBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private Label localTimeZoneLbl;

    @FXML
    private Label welcomeLbl;

    private Stage stage;
    private Parent scene;
    private String fileName = "login_activity.txt";
    private String incorrectPassword;
    private String incorrectPwTitle;
    private String incorrectUserTitle;
    private String incorrectUsername;

    private ZoneId  local = ZoneId.of(TimeZone.getDefault().getID());

    /**Lambda Expression.*/
    LocalTimeZoneInterface setLabel  = localId -> String.valueOf(ZoneId.of(TimeZone.getDefault().getID()));



    /** This method initializes the login screen to the use location and displays login in either French or English.



     * ** discussion of lambda *
     * This lambda expression takes the user local ZoneId  and displays the user Timezone based on the id.
     * This expression automatically handles the determination of the user location to be displayed.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            ResourceBundle rb = ResourceBundle.getBundle("langProp/Nat", Locale.getDefault());

            if(Locale.getDefault().getLanguage().equals("fr") || (Locale.getDefault().getLanguage().equals("en"))) {
                welcomeLbl.setText(rb.getString("welcomeLabel"));
                userLoginLbl.setText(rb.getString("UserLogin"));
                loginBtn.setText(rb.getString("Login"));
                usernameLbl.setText(rb.getString("Username"));
                passwordLbl.setText(rb.getString("Password"));
                closeBtn.setText(rb.getString("Close"));
                incorrectPassword = rb.getString("incorrectPassword");
                incorrectPwTitle = rb.getString("incorrectPwTitle");
                incorrectUsername = rb.getString("incorrectUsername");
                incorrectUserTitle = rb.getString("incorrectUserTitle");
            }

             localTimeZoneLbl.setText(setLabel.hourConverter(local));
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }

    /** This event handler closes the database connection and the application.
     @param event action that triggers method.
     */
    @FXML
    void closeApp(ActionEvent event) {
        DBConnection.closeConnection();
        System.exit(0);
    }

    /** This verifies the user to sign into the application.
     * All successful  and unsuccessful logins are documented to a activity file
     @param event action that triggers method.
     */
    @FXML
    void signIntoApp(ActionEvent event) throws IOException {

        FileWriter fw = new FileWriter(fileName,true);
        PrintWriter outputFile = new PrintWriter(fw);

        String selectStmt = "Select * from users where upper(User_Name) = " + "'" + userNameTxt.getText().toUpperCase() +
                "'" + " and Password = " + "'" + passwordTxt.getText() + "';";

        String selectStmt1 = "Select * from users where upper(User_Name) = " + "'" + userNameTxt.getText().toUpperCase() + "';";


        try{
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(selectStmt);
            ResultSet resultSet = ps.executeQuery();
            PreparedStatement ps1 = DBConnection.getConnection().prepareStatement(selectStmt1);
            ResultSet resultSet1 = ps1.executeQuery();

            String attemptTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"));
            String unsuccessful = "User " + userNameTxt.getText() + " login attempt at " + String.valueOf(attemptTime) + " was unsuccessful." ;
            String successful = "User " + userNameTxt.getText() +" login attempt at "  + String.valueOf(attemptTime) + " was successful.";

            if (resultSet.next()){
                int id = resultSet.getInt("User_ID");
                outputFile.println(successful);

                LocalDateTime currentTime = LocalDateTime.now();
                Long timeDifference = null;
                LocalDateTime appointmentTime;
                String upcomingAppoint = "Upcoming appointment(s)!";
                for (Appointments appointments: DBQuery.getAllAppointments()){

                    appointmentTime = appointments.getStart();
                    timeDifference = ChronoUnit.MINUTES.between(currentTime, appointmentTime);
                    if(appointments.getUserID() == id && (timeDifference > 0 && timeDifference <= 15)) {
                        upcomingAppoint += "\nId:" + appointments.getAppointmentId() + " at " +
                                appointments.getStartTime();
                    }
                }
                if (upcomingAppoint.equals("Upcoming appointment(s)!")){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"No upcoming appointments." );
                    Optional<ButtonType> toDelete = alert.showAndWait();

                }
                else{
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,upcomingAppoint);
                    Optional<ButtonType> toDelete = alert.showAndWait();


                }
                screenSwitch(event, "/view/mainMenu.fxml");
            }
            else if (resultSet1.next()){

                outputFile.println(unsuccessful);
                Alert correctInput = new Alert(Alert.AlertType.ERROR);
                correctInput.setTitle(incorrectPwTitle);
                correctInput.setContentText(incorrectPassword);
                correctInput.showAndWait();
            }
            else {
                Alert correctInput = new Alert(Alert.AlertType.ERROR);
                correctInput.setTitle(incorrectUserTitle);
                correctInput.setContentText(incorrectUsername);
                correctInput.showAndWait();
            }
        }
        catch(SQLException | IOException e){
            e.printStackTrace();
        }
        outputFile.close();

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
