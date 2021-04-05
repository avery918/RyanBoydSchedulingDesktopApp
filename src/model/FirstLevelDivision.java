package model;

public class FirstLevelDivision {

    /** Private fields for a first level division object.*/
    private int divisionId;
    private String division;
    private int countryId;

    /** Constructor to initialize a new first level division object */
    public FirstLevelDivision(int divisionId, String division, int countryId) {
        this.divisionId = divisionId;
        this.division = division;
        this.countryId = countryId;
    }

    public int getDivisionId() {
        return divisionId;
    }


    public String getDivision() {
        return division;
    }


    public int getCountryId() {
        return countryId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    public void setDivision(String division) {
        this.division = division;
    }



    /** Overrides toString() method to return the first level division's name instead.
     * @return  Returns a first level division's name.*/
    @Override
    public String toString(){
        return getDivision();
    }
}
