package model;

public class Countries {

    /** Private fields for a countries object.*/
    private int countryId;
    private String country;

    /** Constructor to initialize a new countries object */
    public Countries(int countryId, String country) {
        this.countryId = countryId;
        this.country = country;
    }


    public int getCountryId() {
        return countryId;
    }

    public String getCountry() {
        return country;
    }


    /** Overrides toString() method to return the country's name instead.
     * @return  Returns a country's name.*/
    @Override
    public String toString(){
        return getCountry();
    }
}
