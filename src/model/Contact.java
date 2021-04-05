package model;

public class Contact {

    /** Private fields for a contact object.*/
    private int contactId;
    private String contactName;
    private String email;

    /** Constructor to initialize a new contact object */
    public Contact(int contactId, String contactName, String email) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.email = email;
    }

    public int getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getEmail() {
        return email;
    }

    /** Overrides toString() method to return the contact's name instead.
     * @return  Returns contact's name.*/
    @Override
    public String toString(){
        return getContactName();
    }
}
