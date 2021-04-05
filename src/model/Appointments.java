package model;

import dbUtils.DBQuery;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class Appointments {

    /** Private fields for an appointment object.*/
    private int appointmentId;
    private String title;
    private String description;
    private  String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime  end;
    private int customerID;
    private int userID;
    private int contactID;
    private String contactName;
    private String startTime;
    private String endTime;


    /** Constructor to initialize a new appointment object */
    public Appointments(int appointmentId, String title, String description, String location, String type,
                        LocalDateTime start, LocalDateTime end, int customerID, int userID, int contactID) {

        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.userID = userID;
        setCustomerID((customerID));
        this.userID = userID;
        setContactID(contactID);
        startTime = getStart().format(DateTimeFormatter.ofPattern("MM-dd-yyy hh:mm a"));
        endTime = getEnd().format(DateTimeFormatter.ofPattern("MM-dd-yyy hh:mm a"));

    }

    public int getAppointmentId() {
        return appointmentId;
    }



    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public int getCustomerID() {
        return customerID;
    }

    /** method to get a customer's name using their ID.
     * @param customerID customer's ID number */
    public void setCustomerID(int customerID) {
        for (Customers customers : DBQuery.getAllCustomers()) {
            if (customerID == customers.getCustomerID() )
                setContactName(customers.getCustomerName());
        }
        this.customerID = customerID;
    }

    public int getUserID() {
        return userID;
    }



    public int getContactID() {
        return contactID;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getContactName() {
        return contactName;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    /** method to get a contact's name using their ID.
     * @param contactID contact's ID number */
    public void setContactID(int contactID) {
        for (Contact contact : DBQuery.getAllContacts()) {
            if (contactID == contact.getContactId() )
                setContactName(contact.getContactName());
        }
        this.contactID = contactID;
    }


    public void setContactName(String contactName) {
        this.contactName = contactName;
    }


    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public String getStartTime() {
        return startTime;
    }

    /** Converts 24hr clock to 12 hr am/pm clock.
     * @param ldt Local time to be converted
     * @return  Returns the converted time as a string*/
    public static String timeConverter(LocalTime ldt){
        String date = "";
        date = ldt.format(DateTimeFormatter.ofPattern("hh:mm a"));
        return date;
    }


    /** Converts 12 hr am/pm clock 24 hr clock.
     * @param time Local time to be converted
     * @return  Returns the converted time as a string*/
    public static String reverseConverter(String time) throws ParseException {
        DateFormat oldFormat = new SimpleDateFormat("hh:mm a");
        DateFormat newFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = oldFormat.parse(time);

        String newTime = newFormat.format(date);
        return newTime;

    }

    /** Performs logic checks
     * @param start start date/time of appointment.
     * @param end end date/time of appointment.
     * @param customerId customer ID.
     * @param appId appointment ID.
     * @return  Returns a string with all logical errors(if any).*/
    public static String logicCheck(LocalDateTime start, LocalDateTime  end, int customerId, int appId){

        String thingsToFix = "";
        LocalDate date = start.toLocalDate();
        LocalDate date1 = end.toLocalDate();
        LocalTime openTime = LocalTime.of(8,0);
        LocalTime closingTime = LocalTime.of(22,0);
        ZoneId businessZone = ZoneId.of("America/New_York");
        ZonedDateTime businessOpenZDT = ZonedDateTime.of(date, openTime, businessZone);
        ZonedDateTime businessCloseZDT = ZonedDateTime.of(date, closingTime, businessZone);

        ZonedDateTime startBusinessZDT =  (start.atZone(ZoneId.of(TimeZone.getDefault().getID()))).withZoneSameInstant(businessZone);
        ZonedDateTime endToBusinessZDT =  (end.atZone(ZoneId.of(TimeZone.getDefault().getID()))).withZoneSameInstant(businessZone);

        if (!date.equals(date1))
            thingsToFix += "Appointment cannot start and end on two different days.\n";

        if (end.isBefore(start) || start.equals(end))
            thingsToFix += "Appointment end time cannot be before or equal to appointment start time.\n";

        if (startBusinessZDT.isBefore(businessOpenZDT) || endToBusinessZDT.isBefore(businessOpenZDT))
            thingsToFix += "Appointment cannot start/end before business hours of 8:00 AM. Please check appointment times.\n";

        if (startBusinessZDT.isAfter(businessCloseZDT) || endToBusinessZDT.isAfter(businessCloseZDT))
            thingsToFix +=  "Appointment cannot start/ends  after business hours of 10:00 PM. Please check appointment time.\n";

        if(DBQuery.overLapCheck(start, end, customerId, appId) != "")
            thingsToFix += DBQuery.overLapCheck(start, end, customerId, appId);

        return thingsToFix;
    }



}