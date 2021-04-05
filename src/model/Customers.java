package model;


import dbUtils.DBQuery;

public class Customers {

    /** Private fields for a customer object.*/
    private int customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionID;
    private String divisionName;

    /** Constructor to initialize a new customer object */
    public Customers(int customerID, String customerName, String address, String postalCode, String phone, int divisionID) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        setDivisionID(divisionID);
    }

    /** Getters and Setters*/
    public int getCustomerID() {
        return customerID;
    }

    public String getCustomerName() {
        return customerName;
    }


    public String getAddress() {
        return address;
    }


    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }


    public int getDivisionID() {
        return divisionID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }



    public String getDivisionName() {
        return divisionName;
    }

    /** Sets first level division name based on the division's Id
     * @param divisionID first level division ID.*/
    public void setDivisionID(int divisionID) {

            for (FirstLevelDivision fld : DBQuery.getAllFirstLevelDivisions()) {
                if (divisionID == fld.getDivisionId() )
                   setDivisionName(fld.getDivision());
            }
        this.divisionID = divisionID;

    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }



    /** Overrides toString() method to return the customer's name instead.
     * @return  Returns customer's name.*/
    @Override
    public String toString(){
        return getCustomerName();
    }

    /** Performs logic checks
     * @param name name of customer.
     * @param address address of customer.
     * @param postal  postal code ofcustomer.
     * @param phone phone number of customer.
     * @param fld first level division customer lives in.
     * @return  Returns a string with all logical errors(if any).*/
    public static String logicCheck(String name, String address, String postal, String phone, FirstLevelDivision fld){

        String thingsToFix = "";

        if(name.isEmpty()){
            thingsToFix += "Enter customer name.\n";
        }
        if(address.isEmpty()){
            thingsToFix += "Enter customer address.\n";
        }
        if (postal.isEmpty()){
            thingsToFix += "Enter customer postal code.\n";
        }
        if (phone.isEmpty()){
            thingsToFix += "Enter customer phone number.\n";
        }
        if (fld == null){
            thingsToFix += "Choose a region for new customer.\n";
        }
        return thingsToFix;
    }

}
