package model;

public class User {

    /** Private fields for a contact object.*/
    private int userID;
    private String userName;
    private String password;


    /** Constructor to initialize a new user object */
    public User(int userID, String userName, String password) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
    }

    /** user getter methods*/

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    /** Overrides toString() method to return the user's name instead.
     * @return  Returns user's name.*/
    @Override
    public String toString(){
        return getUserName();
    }
}
